// PostSearchDomainResponse.java
package org.tikim.sample.domain.board.post.service.domain.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.post.repository.dto.PostSummaryDto;

public record PostSearchDomainResponse(
    Long id,
    String title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long replyCount
) {

    public static PostSearchDomainResponse of(
        PostSummaryDto dto
    ) {
        return new PostSearchDomainResponse(
            dto.id(),
            dto.title(),
            dto.createdAt(),
            dto.updatedAt(),
            dto.replyCount());
    }
}
