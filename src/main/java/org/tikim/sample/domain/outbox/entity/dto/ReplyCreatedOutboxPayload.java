package org.tikim.sample.domain.outbox.entity.dto;

public record ReplyCreatedOutboxPayload(
        Long replyId,
        Long postId,
        Long authorId
) {}