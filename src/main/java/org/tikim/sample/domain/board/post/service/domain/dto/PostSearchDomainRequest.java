// PostCreateDomainRequest.java
package org.tikim.sample.domain.board.post.service.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public record PostSearchDomainRequest(
    @Nullable
    String keyword,
    @NotNull
    Pageable pageable) {

}
