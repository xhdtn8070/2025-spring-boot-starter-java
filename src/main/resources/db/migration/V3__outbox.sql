-- OutboxEvent
CREATE TABLE IF NOT EXISTS `outbox_event` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `event_type` VARCHAR(50) NOT NULL,          -- EnumType.STRING 저장
  `payload` JSON NOT NULL,                    -- 이벤트 페이로드 그대로
  `is_success` BOOLEAN NULL,                  -- NULL: 미처리, TRUE/FALSE: 처리 결과
  `error_reason` TEXT NULL,                   -- 실패 사유(스택 등)
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_outbox_event_deleted` (`is_deleted`),
  INDEX `idx_outbox_event_success_deleted` (`is_success`, `is_deleted`)
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=InnoDB;
