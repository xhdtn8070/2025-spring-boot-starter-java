CREATE TABLE IF NOT EXISTS `tb_post` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT NOT NULL,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_tb_post_deleted` (`is_deleted`)
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=InnoDB;

-- Reply
CREATE TABLE IF NOT EXISTS `tb_reply` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `post_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_tb_reply_post_id_deleted` (`post_id`, `is_deleted`),
    CONSTRAINT `fk_reply_post` FOREIGN KEY (`post_id`) REFERENCES `tb_post`(`id`)
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=InnoDB;
