package com.gesture.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.gesture.backend.repository")
@Profile("prod")
@ConditionalOnProperty(
    prefix = "spring.datasource",
    name = "url"
)
public class DatabaseConfig {
    // 프로덕션 환경에서만 데이터베이스 설정이 활성화됩니다.
}
