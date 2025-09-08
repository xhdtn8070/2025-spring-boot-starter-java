package org.tikim.sample.global.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@Configuration(proxyBeanMethods = false)
@EntityScan(basePackages = "org.tikim")
@EnableJpaRepositories(basePackages = "org.tikim")
public class JpaConfig {
    // 빈 정의 없이 설정 전용 클래스
}
