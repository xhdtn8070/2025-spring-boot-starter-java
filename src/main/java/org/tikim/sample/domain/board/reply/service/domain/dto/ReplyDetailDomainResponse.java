// ReplyDetailDomainResponse.java
package org.tikim.sample.domain.board.reply.service.domain.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.reply.entity.Reply;

public record ReplyDetailDomainResponse(
    Long id,
    Long authorId,
    Long postId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ReplyDetailDomainResponse of(Reply r) {
        return new ReplyDetailDomainResponse(
            r.getId(),
            r.getAuthorId(),
            r.getPost().getId(),
            r.getContent(),
            r.getCreatedAt(),
            r.getUpdatedAt()
        );
    }

    public boolean isWriter(Long userId) {
        return this.authorId.equals(userId);
    }
}
