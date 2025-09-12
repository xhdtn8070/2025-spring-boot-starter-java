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


}
