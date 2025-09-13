// org.tikim.sample.infra.outbox.JpaOutboxAppender
package org.tikim.sample.domain.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tikim.sample.domain.outbox.entity.OutboxEvent;
import org.tikim.sample.domain.outbox.entity.dto.OutboxEventType;
import org.tikim.sample.domain.outbox.port.OutboxAppender;
import org.tikim.sample.domain.outbox.repository.OutboxEventRepository;

@Component
@RequiredArgsConstructor
public class JpaOutboxAppenderService implements OutboxAppender {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void append(OutboxEventType type, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            repository.save(OutboxEvent.of(type, json));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to append outbox", e);
        }
    }
}
