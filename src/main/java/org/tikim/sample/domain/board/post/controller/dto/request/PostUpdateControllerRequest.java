// PostUpdateControllerRequest.java
package org.tikim.sample.domain.board.post.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateControllerRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content
) {}
