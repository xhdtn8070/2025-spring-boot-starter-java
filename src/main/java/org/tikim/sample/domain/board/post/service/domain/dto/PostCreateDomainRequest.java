// PostCreateDomainRequest.java
package org.tikim.sample.domain.board.post.service.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCreateDomainRequest(
    @NotNull
    Long authorId,
    @NotBlank
    String title,
    @NotBlank
    String content) {
}
