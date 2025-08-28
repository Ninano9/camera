package com.gesture.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
public class GestureBackendApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Gesture Backend 시작!");
        SpringApplication.run(GestureBackendApplication.class, args);
        System.out.println("✅ Gesture Backend 시작 완료!");
    }

    @RestController
    @CrossOrigin(origins = "*")
    public static class SimpleController {
        
        @GetMapping("/")
        public String root() {
            System.out.println("🏠 루트 페이지 요청");
            return "🚀 Gesture Backend is running! 2025-08-28-FINAL";
        }
        
        @GetMapping("/api/health")
        public String health() {
            System.out.println("💚 헬스체크 요청");
            return "{\"status\": \"UP\", \"message\": \"Backend is working!\"}";
        }
        
        @GetMapping("/api/gesture/test")
        public String gestureTest() {
            System.out.println("🎯 제스처 테스트 요청");
            return "🎯 Gesture API is working!";
        }
        
        @PostMapping("/api/gesture/mouse/move")
        public String mouseMove(@RequestBody(required = false) String body) {
            System.out.println("🖱️ 마우스 이동: " + body);
            return "{\"success\": true, \"message\": \"Mouse move OK\"}";
        }
        
        @PostMapping("/api/gesture/mouse/left-click")
        public String leftClick() {
            System.out.println("🖱️ 좌클릭");
            return "{\"success\": true, \"message\": \"Left click OK\"}";
        }
        
        @PostMapping("/api/gesture/mouse/right-click")
        public String rightClick() {
            System.out.println("🖱️ 우클릭");
            return "{\"success\": true, \"message\": \"Right click OK\"}";
        }
        
        @PostMapping("/api/gesture/mouse/scroll")
        public String scroll(@RequestBody(required = false) String body) {
            System.out.println("📜 스크롤: " + body);
            return "{\"success\": true, \"message\": \"Scroll OK\"}";
        }
    }
}