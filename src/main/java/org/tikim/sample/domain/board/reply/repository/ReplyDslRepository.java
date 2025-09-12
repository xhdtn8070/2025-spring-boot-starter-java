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

    // 특정 게시글의 최신 N개
    List<ReplySummaryDto> latestByPostId(Long postId, int limit);

    // 댓글 단건 상세를 Post까지 fetch join
    Optional<Reply> findWithPost(Long replyId);

    // 특정 게시글의 댓글 수
    long countByPostId(Long postId);

    // 내용/게시글 제목 키워드 검색 페이징
    Page<ReplySummaryDto> searchByKeyword(String keyword, Pageable pageable);
}
