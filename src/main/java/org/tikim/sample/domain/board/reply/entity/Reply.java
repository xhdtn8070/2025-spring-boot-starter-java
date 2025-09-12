// Reply.java
package org.tikim.sample.domain.board.reply.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.tikim.sample.domain.board.post.entity.Post;
import org.tikim.sample.domain.board.reply.service.domain.dto.ReplyCreateDomainRequest;
import org.tikim.sample.domain.board.reply.service.domain.dto.ReplyUpdateDomainRequest;
import org.tikim.sample.global.jpa.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용
@Builder(access = AccessLevel.PRIVATE) // 정적 팩토리 메서드 사용 유도
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "tb_reply")
@SoftDelete(columnName = "is_deleted")
public class Reply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;


    // 정적 팩토리
    public static Reply of(Post post, ReplyCreateDomainRequest req) {
        return Reply.builder()
                .post(post)
                .content(req.content())
                .build();
    }


    public void update(ReplyUpdateDomainRequest req) {
        content = req.content();
    }
}
