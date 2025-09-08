package org.tikim.sample.redis.lock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.tikim.sample.container.SpringIntegrationTestSupport;
import org.tikim.sample.global.redis.service.RedisLockService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class RedisLockServiceTest extends SpringIntegrationTestSupport {

    @Autowired RedisLockService redisLockService;
    @Autowired RedissonClient redisson;

    private static String key(String p){ return p + ":" + UUID.randomUUID(); }

    /** 다른 스레드에서 선점/해제 제어 유틸 */
    private AutoCloseable lockInOtherThread(String key, Duration lease, CountDownLatch releaseSignal) throws Exception {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        CountDownLatch locked = new CountDownLatch(1);
        Future<?> f = ex.submit(() -> {
            RLock l = redisson.getLock(key);
            l.lock(lease.toSeconds(), TimeUnit.SECONDS);
            try { locked.countDown(); releaseSignal.await(); }
            catch (InterruptedException ignored){ Thread.currentThread().interrupt(); }
            finally { if (l.isHeldByCurrentThread()) l.unlock(); }
        });
        if (!locked.await(2, TimeUnit.SECONDS)) throw new IllegalStateException("pre-lock failed");
        return () -> { releaseSignal.countDown(); ex.shutdownNow(); try { f.get(1, TimeUnit.SECONDS);} catch(Exception ignored){} };
    }

    @Test
    @DisplayName("기본: 대기하지 않음 — 선점 중이면 실행되지 않는다")
    void noWait_mode() throws Exception {
        String k = key("test:lock");
        CountDownLatch release = new CountDownLatch(1);
        try (var ignored = lockInOtherThread(k, Duration.ofSeconds(3), release)) {
            boolean executed = redisLockService.executeWithLock(k, lock -> {
                throw new AssertionError("should not run while contended");
            });
            assertThat(executed).isFalse();
        }
    }

    @Test
    @DisplayName("대기모드: 기다려서 획득 — 상대가 해제하면 실행된다")
    void wait_mode() throws Exception {
        String k = key("test:lock");
        CountDownLatch release = new CountDownLatch(1);
        try (var ignored = lockInOtherThread(k, Duration.ofSeconds(1), release)) {
            // 300ms 뒤 해제
            Executors.newSingleThreadScheduledExecutor().schedule(release::countDown, 300, TimeUnit.MILLISECONDS);

            List<String> sink = new ArrayList<>();
            boolean executed = redisLockService.executeWithLock(
                    k, true, Duration.ofSeconds(5), Duration.ofSeconds(30),
                    lock -> sink.add("ran")
            );
            assertThat(executed).isTrue();
            assertThat(sink).containsExactly("ran");
        }
    }
}
