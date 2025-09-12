// PostDetailServiceResponse.java
package org.tikim.sample.domain.board.post.service.application.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.post.service.domain.dto.PostDetailDomainResponse;

public record PostDetailServiceResponse(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static PostDetailServiceResponse of(PostDetailDomainResponse post) {
        return new PostDetailServiceResponse(
            post.id(),
            post.title(),
            post.content(),
            post.createdAt(),
            post.updatedAt()
        );
    }
}
