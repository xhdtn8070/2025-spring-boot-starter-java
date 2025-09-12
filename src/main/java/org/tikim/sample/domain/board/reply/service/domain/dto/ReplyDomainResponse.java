// ReplyDomainResponse.java
package org.tikim.sample.domain.board.reply.service.domain.dto;

import java.time.LocalDateTime;

public record ReplyDomainResponse(
        Long id, Long authorId, Long postId, String content, LocalDateTime createdAt
) {}
