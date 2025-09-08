package org.tikim.sample.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    /** 기본 보유시간(lease time) */
    private static final Duration DEFAULT_LEASE = Duration.ofSeconds(60);
    /** 대기하여 획득할 때의 기본 대기시간(wait time) */
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(5);

    /**
     * 기본 버전: 대기하지 않고 즉시 시도. 실패 시 action 미실행, false 반환.
     */
    public boolean executeWithLock(String lockKey, Consumer<RedissonLock> action) {
        return executeWithLock(lockKey, false, DEFAULT_WAIT, DEFAULT_LEASE, action);
    }

    /**
     * 확장 버전: waitIfBusy=true면 waitTime 동안 대기해서라도 획득 시도.
     * - waitIfBusy=false: 즉시 시도(대기 0), 실패 시 false
     * - waitIfBusy=true : waitTime까지 대기하며 시도, 성공 시 action 실행
     */
    public boolean executeWithLock(String lockKey,
                                   boolean waitIfBusy,
                                   Duration waitTime,
                                   Duration leaseTime,
                                   Consumer<RedissonLock> action) {
        Objects.requireNonNull(lockKey, "lockKey");
        Objects.requireNonNull(waitTime, "waitTime");
        Objects.requireNonNull(leaseTime, "leaseTime");
        Objects.requireNonNull(action, "action");

        final RLock rlock = redissonClient.getLock(lockKey);
        final long wait = waitIfBusy ? waitTime.toMillis() : 0L;
        final long lease = leaseTime.toMillis();

        boolean acquired = false;
        try (RedissonLock lock = new RedissonLock(rlock)) {
            acquired = rlock.tryLock(wait, lease, TimeUnit.MILLISECONDS);
            if (acquired) {
                lock.markOwned();               // close()에서만 해제되도록 플래그
                action.accept(lock);
                return true;
            }
            return false;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** AutoCloseable 핸들: 보유 중일 때만 안전 해제 */
    public static final class RedissonLock implements AutoCloseable {
        private final RLock delegate;
        private boolean owned;

        private RedissonLock(RLock delegate) {
            this.delegate = delegate;
        }

        private void markOwned() { this.owned = true; }

        /** 현재 스레드가 보유 중인지 확인 */
        public boolean isHeldByCurrentThread() {
            return delegate.isHeldByCurrentThread();
        }

        @Override
        public void close() {
            if (!owned) return;
            try {
                if (delegate.isHeldByCurrentThread()) {
                    delegate.unlock();
                }
            } catch (IllegalMonitorStateException ignored) {
                // 이미 풀렸거나 소유 스레드가 아님
            }
        }
    }
}
