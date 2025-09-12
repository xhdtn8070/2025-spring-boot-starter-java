package org.tikim.sample.domain.board.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tikim.sample.domain.board.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
