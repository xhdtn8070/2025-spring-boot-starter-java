// PostCreateDomainRequest.java
package org.tikim.sample.domain.board.post.service.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record PostCreateDomainRequest(
    @NotBlank
    String title,
    @NotBlank
    String content) {
}
