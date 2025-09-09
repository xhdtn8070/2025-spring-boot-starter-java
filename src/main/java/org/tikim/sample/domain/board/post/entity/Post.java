// Post.java
package org.tikim.sample.domain.board.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.SoftDelete;
import org.tikim.sample.domain.board.reply.entity.Reply;
import org.tikim.sample.global.jpa.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tb_post")
@SoftDelete(columnName = "is_deleted") // deleted = true면 조회에서 제외
public class Post extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.REMOVE,      // 네 스타일대로 REMOVE만
        orphanRemoval = true               // 컬렉션에서 빼면 자식 remove
    )
    private List<Reply> replies = new ArrayList<>();

}
