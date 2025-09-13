// OutboxRelayScheduler.java
package org.tikim.sample.domain.outbox.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tikim.sample.domain.outbox.service.OutboxRelayService;
import org.tikim.sample.global.redis.service.RedisLockService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final RedisLockService redisLockService;
    private final OutboxRelayService outboxRelayService;

    @Value("${outbox.relay.batch-size:10}")
    private int batchSize;

    private static final String OUTBOX_LOCK_KEY = "outbox-relay-lock";
    /**
     * fixedDelay: 이전 실행이 끝난 시점 기준 지연
     * initialDelay: 앱 기동 후 첫 실행 지연
     */
    @Scheduled(
        fixedDelayString = "${outbox.relay.fixed-delay:1000}",
        initialDelayString = "${outbox.relay.initial-delay:5000}"
    )
    public void run() {
        redisLockService.executeWithLock(OUTBOX_LOCK_KEY, lock -> {
            log.info("[OUTBOX] relay job started");
            int processed = outboxRelayService.runBatch(batchSize);
            log.info("[OUTBOX] relay job finished, processed {} events", processed);
        });
    }
}
