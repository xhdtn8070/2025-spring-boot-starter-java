// PostDetailDomainResponse.java
package org.tikim.sample.domain.board.post.service.domain.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.post.entity.Post;

public record PostDetailDomainResponse(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static PostDetailDomainResponse of(Post post) {
        return new PostDetailDomainResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}
