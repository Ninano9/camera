package com.gesture.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.gesture.backend.controller.GestureController;
import com.gesture.backend.controller.HealthController;

@SpringBootApplication(scanBasePackages = "com.gesture.backend")
@Import({GestureController.class, HealthController.class})
public class GestureBackendApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Gesture Backend 시작 중...");
        System.out.println("📦 ComponentScan 패키지: com.gesture.backend");
        System.out.println("⚡ 경량화된 제스처 전용 백엔드 - JPA/DB 의존성 완전 제거");
        System.out.println("🔍 현재 디렉토리 스캔:");
        System.out.println("  - com.gesture.backend.controller");
        System.out.println("  - com.gesture.backend.service");
        System.out.println("  - com.gesture.backend.config");
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
        
        @GetMapping("/api/gesture/test")
        public ResponseEntity<String> gestureTest() {
            return ResponseEntity.ok("🎯 Gesture Controller is working!");
        }
        
        @PostMapping("/api/gesture/mouse/move")
        public ResponseEntity<String> mouseMove(@RequestBody String body) {
            System.out.println("🖱️ 마우스 이동 요청 받음: " + body);
            return ResponseEntity.ok("마우스 이동 완료");
        }
        
        @PostMapping("/api/gesture/mouse/left-click")
        public ResponseEntity<String> leftClick() {
            System.out.println("🖱️ 좌클릭 요청 받음");
            return ResponseEntity.ok("좌클릭 완료");
        }
        
        @PostMapping("/api/gesture/mouse/scroll")
        public ResponseEntity<String> scroll(@RequestBody String body) {
            System.out.println("📜 스크롤 요청 받음: " + body);
            return ResponseEntity.ok("스크롤 완료");
        }
    }
}
