// Reply.java
package org.tikim.sample.domain.board.reply.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.SoftDelete;
import org.tikim.sample.domain.board.post.entity.Post;
import org.tikim.sample.global.jpa.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tb_reply")
@SoftDelete(columnName = "is_deleted")
public class Reply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;


}
