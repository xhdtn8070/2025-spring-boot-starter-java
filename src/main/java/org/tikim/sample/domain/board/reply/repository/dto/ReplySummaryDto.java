// ReplySummaryDto.java
package org.tikim.sample.domain.board.reply.repository.dto;

import java.time.LocalDateTime;

public record ReplySummaryDto(
        Long id,
        Long postId,
        String content,
        LocalDateTime createdAt
) {}
