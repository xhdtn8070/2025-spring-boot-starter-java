// OutboxRelayService.java
package org.tikim.sample.domain.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tikim.sample.domain.board.reply.event.ReplyEventPublisher;
import org.tikim.sample.domain.outbox.dto.ReplyCreatedOutboxPayload;
import org.tikim.sample.domain.outbox.entity.OutboxEvent;
import org.tikim.sample.domain.outbox.repository.OutboxEventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private final OutboxEventRepository outboxEventRepository;
    private final ReplyEventPublisher replyEventPublisher;
    private final ObjectMapper objectMapper; // payload 파싱용

    @Transactional
    public int runBatch(int batchSize) {
        Page<OutboxEvent> page = outboxEventRepository.findByIsSuccessIsNull(
            PageRequest.of(0, batchSize, Sort.by(Sort.Direction.ASC, "id"))
        );
        if (page.isEmpty()) {
            return 0;
        }

        List<OutboxEvent> events = page.getContent();
        log.info("[OUTBOX] processing {} events", events.size());

        for (OutboxEvent e : events) {
            try {
                switch (e.getEventType()) {
                    case REPLY_CREATED -> {
                        var p = objectMapper.readValue(e.getPayload(), ReplyCreatedOutboxPayload.class);
                        replyEventPublisher.publishReplyCreated(p);  // 한 번만 발행
                    }
                    default -> throw new IllegalArgumentException("Unsupported event type: " + e.getEventType());
                }
                e.markAsSuccess();
            } catch (Throwable ex) {
                e.markAsFailed(buildError(ex));
                log.error("[OUTBOX] handle failed. id={}, err={}", e.getId(), ex.toString(), ex);
            }
        }
        outboxEventRepository.saveAll(events);
        return events.size();
    }

    private static String buildError(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String trace = sw.toString();
        return (e.getMessage() == null ? "Outbox handle failed" : e.getMessage()) + "\n" + trace;
    }
}
