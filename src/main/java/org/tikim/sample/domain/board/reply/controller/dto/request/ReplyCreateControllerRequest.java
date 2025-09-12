// ReplyCreateControllerRequest.java
package org.tikim.sample.domain.board.reply.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReplyCreateControllerRequest(
        @NotBlank String content
) {}
