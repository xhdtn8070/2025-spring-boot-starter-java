// org.tikim.sample.domain.outbox.port.OutboxAppender
package org.tikim.sample.domain.outbox.port;

import org.tikim.sample.domain.outbox.entity.dto.OutboxEventType;

public interface OutboxAppender {
    void append(OutboxEventType type, Object payload);
}
