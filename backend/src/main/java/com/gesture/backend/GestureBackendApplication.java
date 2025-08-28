package com.gesture.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class, 
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class
})
@ComponentScan(basePackages = "com.gesture.backend")
@EntityScan(basePackages = "com.gesture.backend.nonexistent")
public class GestureBackendApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Gesture Backend 시작 중...");
        System.out.println("📦 ComponentScan 패키지: com.gesture.backend");
        System.out.println("🚫 JPA/DataSource 비활성화 - 제스처 기능만 활성화");
        SpringApplication.run(GestureBackendApplication.class, args);
        System.out.println("✅ Gesture Backend 시작 완료!");
    }

    @RestController
    public static class RootController {
        
        @GetMapping("/")
        public ResponseEntity<String> root() {
            return ResponseEntity.ok("Gesture Backend is running! Version: 2025-08-28-v2");
        }
        
        @GetMapping("/api")
        public ResponseEntity<String> apiRoot() {
            return ResponseEntity.ok("API is available!");
        }
    }
}
