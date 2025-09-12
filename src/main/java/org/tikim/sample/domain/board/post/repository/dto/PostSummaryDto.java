// PostSummaryDto.java
package org.tikim.sample.domain.board.post.repository.dto;

import java.time.LocalDateTime;

public record PostSummaryDto(
    Long id,
    String title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long replyCount
) {

}
