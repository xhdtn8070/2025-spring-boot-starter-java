// ReplyControllerResponse.java
package org.tikim.sample.domain.board.reply.controller.dto.response;

import java.time.LocalDateTime;

public record ReplyControllerResponse(
        Long id, Long postId, String content, LocalDateTime createdAt
) {}
