// ReplyServiceResponse.java  (기존 클래스에 of 추가)
package org.tikim.sample.domain.board.reply.service.application.dto;

import java.time.LocalDateTime;
import org.tikim.sample.domain.board.reply.service.domain.dto.ReplyDomainResponse;

public record ReplyServiceResponse(
    Long id,
    Long postId,
    String content,
    LocalDateTime createdAt
) {
    public static ReplyServiceResponse of(ReplyDomainResponse d) {
        return new ReplyServiceResponse(d.id(), d.postId(), d.content(), d.createdAt());
    }
}
