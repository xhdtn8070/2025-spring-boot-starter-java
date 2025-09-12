// ReplyDetailServiceResponse.java
package org.tikim.sample.domain.board.reply.service.application.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.reply.service.domain.dto.ReplyDetailDomainResponse;

public record ReplyDetailServiceResponse(
    Long id,
    Long authorId,
    Long postId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static ReplyDetailServiceResponse of(ReplyDetailDomainResponse d) {
        return new ReplyDetailServiceResponse(
            d.id(), d.authorId(), d.postId(), d.content(), d.createdAt(), d.updatedAt()
        );
    }
}
