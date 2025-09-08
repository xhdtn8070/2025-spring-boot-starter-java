package org.tikim.sample.redis.lock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonFairLockTest extends SpringIntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("FairLock은 요청 순서(FIFO)대로 점유된다")
    void fairLockHonorsFifoOrder() throws Exception {
        final String lockName = "lock:test:fair";
        final RLock fairLock = redissonClient.getFairLock(lockName);
        final int threadCount = 5;
        final List<Integer> acquisitionOrder = new CopyOnWriteArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch done = new CountDownLatch(threadCount);

        // 미리 락을 점유해서 대기열을 만들고 시작
        fairLock.lock();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    // 의도적으로 요청 시점을 순차로
                    sleep(idx * 100L);
                    fairLock.lock(30, TimeUnit.SECONDS);
                    try {
                        acquisitionOrder.add(idx);
                    } finally {
                        fairLock.unlock();
                    }
                } finally {
                    done.countDown();
                }
            });
        }

        // 모든 스레드가 요청을 보낼 시간
        sleep(1000);

        // 초기 락 해제 → FIFO로 순차 점유 시작
        fairLock.unlock();

        done.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(acquisitionOrder).containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    @DisplayName("FairLock은 FIFO를 보장하지만 일반 RLock은 순서가 달라질 수 있다")
    void compareFairLockAndRegularLockOrdering() throws Exception {
        final RLock fairLock = redissonClient.getFairLock("lock:test:fair:vs:regular");
        final RLock regularLock = redissonClient.getLock("lock:test:regular:comparison");

        final int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount * 2);

        final List<Integer> fairReqOrder = new CopyOnWriteArrayList<>();
        final List<Integer> fairAcqOrder = new CopyOnWriteArrayList<>();
        final List<Integer> regularReqOrder = new CopyOnWriteArrayList<>();
        final List<Integer> regularAcqOrder = new CopyOnWriteArrayList<>();

        fairLock.lock();
        regularLock.lock();

        CountDownLatch fairDone = new CountDownLatch(threadCount);
        CountDownLatch regularDone = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;

            // FairLock 요청 (역순 딜레이)
            executor.submit(() -> {
                sleep((threadCount - id) * 100L);
                synchronized (fairReqOrder) { fairReqOrder.add(id); }
                fairLock.lock(30, TimeUnit.SECONDS);
                try {
                    fairAcqOrder.add(id);
                } finally {
                    fairLock.unlock();
                    fairDone.countDown();
                }
            });

            // Regular Lock 요청 (역순 딜레이)
            executor.submit(() -> {
                sleep((threadCount - id) * 100L);
                synchronized (regularReqOrder) { regularReqOrder.add(id); }
                regularLock.lock(30, TimeUnit.SECONDS);
                try {
                    regularAcqOrder.add(id);
                } finally {
                    regularLock.unlock();
                    regularDone.countDown();
                }
            });
        }

        sleep(1000);
        fairLock.unlock();
        regularLock.unlock();

        fairDone.await(30, TimeUnit.SECONDS);
        regularDone.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // FairLock은 요청 순서와 획득 순서가 동일(FIFO)
        assertThat(fairAcqOrder).containsExactlyElementsOf(fairReqOrder);

        // 일반 RLock은 스케줄링/타이밍에 따라 순서가 다를 수 있음 (검증은 비교 출력만)
        System.out.println("Regular request order:  " + regularReqOrder);
        System.out.println("Regular acquire order: " + regularAcqOrder);
    }

    @Test
    @DisplayName("FairLock은 재진입 가능하며 holdCount가 증가/감소한다")
    void reentrantLockWorks() throws Exception {
        final RLock lock = redissonClient.getFairLock("lock:test:fair:reentrant");

        boolean first = lock.tryLock(1, 10, TimeUnit.SECONDS);
        boolean second = lock.tryLock(1, 10, TimeUnit.SECONDS);

        assertThat(first).isTrue();
        assertThat(second).isTrue();
        assertThat(lock.getHoldCount()).isEqualTo(2);

        lock.unlock();
        lock.unlock();

        // Redis 반영 대기
        sleep(100);

        assertThat(lock.isLocked()).isFalse();
        assertThat(lock.getHoldCount()).isZero();
    }

    // ---- helpers
    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}
