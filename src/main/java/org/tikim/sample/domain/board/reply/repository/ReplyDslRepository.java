// ReplyRepositoryCustom.java
package org.tikim.sample.domain.board.reply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tikim.sample.domain.board.reply.entity.Reply;
import org.tikim.sample.domain.board.reply.repository.dto.ReplySummaryDto;

import java.util.List;
import java.util.Optional;

public interface ReplyDslRepository {

    // 특정 게시글의 댓글 페이징
    Page<ReplySummaryDto> findPageByPostId(Long postId, Pageable pageable);

}
