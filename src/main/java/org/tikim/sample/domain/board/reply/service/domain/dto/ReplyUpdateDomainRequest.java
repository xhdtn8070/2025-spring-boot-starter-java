// ReplyUpdateDomainRequest.java
package org.tikim.sample.domain.board.reply.service.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyUpdateDomainRequest(
    @NotBlank
    String content
) {}
