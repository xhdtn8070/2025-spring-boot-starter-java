// ReplyRepositoryImpl.java
package org.tikim.sample.domain.board.reply.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.tikim.sample.domain.board.reply.entity.Reply;
import org.tikim.sample.domain.board.reply.repository.dto.ReplySummaryDto;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static org.tikim.sample.domain.board.post.entity.QPost.post;
import static org.tikim.sample.domain.board.reply.entity.QReply.reply;

@Repository
@RequiredArgsConstructor
public class ReplyDslDslRepositoryImpl implements ReplyDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReplySummaryDto> findPageByPostId(Long postId, Pageable pageable) {
        List<ReplySummaryDto> content = queryFactory
                .select(constructor(
                        ReplySummaryDto.class,
                        reply.id,
                        reply.post.id,
                        reply.content,
                        reply.createdAt
                ))
                .from(reply)
                .where(reply.post.id.eq(postId))
                .orderBy(reply.createdAt.desc(), reply.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(reply.id.count())
                .from(reply)
                .where(reply.post.id.eq(postId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

}
