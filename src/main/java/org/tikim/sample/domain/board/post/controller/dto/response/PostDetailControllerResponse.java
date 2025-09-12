// PostDetailControllerResponse.java
package org.tikim.sample.domain.board.post.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.tikim.sample.domain.board.reply.controller.dto.response.ReplyControllerResponse;

public record PostDetailControllerResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
