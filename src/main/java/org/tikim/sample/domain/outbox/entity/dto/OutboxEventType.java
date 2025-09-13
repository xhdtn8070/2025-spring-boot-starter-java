package org.tikim.sample.domain.outbox.entity.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxEventType {
    REPLY_CREATED,

}
