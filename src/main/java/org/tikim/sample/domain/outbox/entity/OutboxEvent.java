package org.tikim.sample.domain.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.SqlTypes;
import org.tikim.sample.domain.outbox.entity.dto.OutboxEventType;
import org.tikim.sample.global.jpa.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "outbox_event")
@SoftDelete(columnName = "is_deleted")
public class OutboxEvent extends BaseEntity {

    @Column(name = "event_type")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Enumerated(EnumType.STRING)
    private OutboxEventType eventType; // ← 그대로 두되 "카테고리" 용도

    @Column(columnDefinition = "JSON")
    private String payload;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "error_reason", columnDefinition = "TEXT")
    private String errorReason;

    //성공 했을때 부르는 함수
    public void markAsSuccess() {
        this.isSuccess = true;
        this.errorReason = null;
    }
    //실패 했을때 부르는 함수
    public void markAsFailed(String errorReason) {
        this.isSuccess = false;
        this.errorReason = errorReason;
    }
}
