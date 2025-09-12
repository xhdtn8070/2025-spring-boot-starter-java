package org.tikim.sample.domain.outbox.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.tikim.sample.domain.outbox.entity.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Page<OutboxEvent> findByIsSuccessIsNull(Pageable pageable);
}
