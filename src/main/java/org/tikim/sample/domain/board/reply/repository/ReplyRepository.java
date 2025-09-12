package org.tikim.sample.domain.board.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tikim.sample.domain.board.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
