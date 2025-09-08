package org.tikim.sample.redis.lock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonLockTest extends SpringIntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("분산 락 기본: tryLock → unlock → 해제 확인")
    void basicLock() throws Exception {
        String lockName = "lock:test:basic";
        RLock lock = redissonClient.getLock(lockName);

        boolean locked = lock.tryLock(1, 5, TimeUnit.SECONDS);

        assertThat(locked).isTrue();
        assertThat(lock.isHeldByCurrentThread()).isTrue();

        lock.unlock();
        assertThat(lock.isLocked()).isFalse();
    }

    @Test
    @DisplayName("재진입 가능한 락: 두 번 획득 후 두 번 해제하면 완전 해제된다")
    void reentrantLock() throws Exception {
        RLock lock = redissonClient.getLock("lock:test:reentrant");

        boolean firstAcquire = lock.tryLock(1, 10, TimeUnit.SECONDS);
        boolean secondAcquire = lock.tryLock(1, 10, TimeUnit.SECONDS);

        assertThat(firstAcquire).isTrue();
        assertThat(secondAcquire).isTrue();
        assertThat(lock.getHoldCount()).isEqualTo(2);

        lock.unlock();
        lock.unlock();

        // 환경에 따라 필요시 약간 대기해도 됨
        // Thread.sleep(100);

        assertThat(lock.isLocked()).isFalse();
        assertThat(lock.getHoldCount()).isZero();
    }

    @Test
    @DisplayName("경합 상황: 메인 스레드가 점유 중일 때 다른 스레드는 tryLock 실패")
    void contention() throws Exception {
        RLock lock = redissonClient.getLock("lock:test:contend");
        ExecutorService executor = Executors.newSingleThreadExecutor();

        lock.lock(5, TimeUnit.SECONDS); // 메인 스레드가 선점

        Future<Boolean> future = executor.submit(() -> {
            boolean acquired = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (acquired) {
                try { /* nothing */ }
                finally { lock.unlock(); }
            }
            return acquired;
        });

        boolean otherThreadAcquired = future.get(3, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(otherThreadAcquired).isFalse(); // 다른 스레드 획득 실패가 기대

        lock.unlock(); // 메인 스레드 해제
        assertThat(lock.isLocked()).isFalse(); // 완전 해제 확인
    }

    @Test
    @DisplayName("강제 해제(forceUnlock): 잠겨있을 때 강제 해제 가능")
    void forceUnlock() {
        RLock lock = redissonClient.getLock("lock:test:force");

        lock.lock(10, TimeUnit.SECONDS);

        boolean stillLocked = lock.isLocked();
        boolean forced = lock.forceUnlock();
        boolean unlocked = !lock.isLocked();

        assertThat(stillLocked).isTrue();
        assertThat(forced).isTrue();
        assertThat(unlocked).isTrue();
    }
}
