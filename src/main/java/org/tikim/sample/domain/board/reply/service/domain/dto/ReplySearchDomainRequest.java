// ReplySearchDomainRequest.java
package org.tikim.sample.domain.board.reply.service.domain.dto;

import org.springframework.data.domain.Pageable;

public record ReplySearchDomainRequest(
    Long postId,
    Pageable pageable
) {}
