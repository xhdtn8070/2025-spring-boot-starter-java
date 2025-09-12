// PostSummaryControllerResponse.java
package org.tikim.sample.domain.board.post.controller.dto.response;

import java.time.LocalDateTime;

public record PostSummaryControllerResponse(
    Long id, Long authorId,
    String title, LocalDateTime createdAt, LocalDateTime updatedAt, long replyCount
) {

}
