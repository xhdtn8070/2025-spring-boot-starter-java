// org.tikim.sample.domain.outbox.dto.ReplyCreatedOutboxPayload
package org.tikim.sample.domain.outbox.dto;

public record ReplyCreatedOutboxPayload(
    Long replyId,
    Long postId,
    Long postOwnerId,
    Long replyAuthorId
) {}
