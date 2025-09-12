// PostSummaryServiceResponse.java
package org.tikim.sample.domain.board.post.service.application.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.post.service.domain.dto.PostSearchDomainResponse;

public record PostSummaryServiceResponse(
    Long id,
    Long authorId,
    String title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long replyCount
) {

    public static PostSummaryServiceResponse of(
        PostSearchDomainResponse dto
    ) {
        return new PostSummaryServiceResponse(
            dto.id(),
            dto.authorId(),
            dto.title(),
            dto.createdAt(),
            dto.updatedAt(),
            dto.replyCount()
        );
    }
}
