// ReplyUpdateControllerRequest.java
package org.tikim.sample.domain.board.reply.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyUpdateControllerRequest(
    @NotBlank(message = "content is required")
    @Size(max = 1000, message = "content must be <= 1000 chars")
    String content
) {}
