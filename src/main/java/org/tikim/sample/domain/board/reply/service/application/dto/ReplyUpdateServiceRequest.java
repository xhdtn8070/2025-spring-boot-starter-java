// ReplyUpdateServiceRequest.java
package org.tikim.sample.domain.board.reply.service.application.dto;

public record ReplyUpdateServiceRequest(
    Long replyId,
    String content
) {}
