// PostRepositoryImpl.java
package org.tikim.sample.domain.board.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.tikim.sample.domain.board.post.entity.Post;
import org.tikim.sample.domain.board.post.repository.dto.PostSummaryDto;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static org.tikim.sample.domain.board.post.entity.QPost.post;
import static org.tikim.sample.domain.board.reply.entity.QReply.reply;

@Repository
@RequiredArgsConstructor
public class PostDslDslRepositoryImpl implements PostDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSummaryDto> search(String keyword, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            where.and(post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }

        // content
        List<PostSummaryDto> content = queryFactory
                .select(constructor(
                        PostSummaryDto.class,
                        post.id,
                        post.title,
                        post.createdAt,
                        post.updatedAt,
                        reply.id.countDistinct()
                ))
                .from(post)
                .leftJoin(post.replies, reply)
                .where(where)
                .groupBy(post.id, post.title, post.createdAt)
                .orderBy(post.createdAt.desc())          // 정렬은 Pageable.sort 로도 확장 가능
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // total
        Long total = queryFactory
                .select(post.id.count())
                .from(post)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

}
