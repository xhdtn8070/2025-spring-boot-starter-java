// PostRepositoryCustom.java
package org.tikim.sample.domain.board.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tikim.sample.domain.board.post.entity.Post;

import java.util.List;
import java.util.Optional;
import org.tikim.sample.domain.board.post.repository.dto.PostSummaryDto;

public interface PostDslRepository {

    // 제목/본문 키워드 검색 + 페이지네이션 + 정렬(Pageable)
    Page<PostSummaryDto> search(String keyword, Pageable pageable);

    // Post + replies fetch join (단건 상세)
    Optional<Post> findWithReplies(Long postId);

    // 댓글수 순 상위 N개 요약
    List<PostSummaryDto> topNByReplyCount(int limit);

    // 키워드 기준 총 개수
    long countByKeyword(String keyword);

    // 제목 존재 여부
    boolean existsByTitle(String title);
}
