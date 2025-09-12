// ReplyCreateDomainRequest.java
package org.tikim.sample.domain.board.reply.service.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReplyCreateDomainRequest(
    @NotNull
    Long postId,
    @NotBlank
    String content
) {}
