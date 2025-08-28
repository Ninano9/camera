package com.gesture.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.gesture.backend.service.MouseControlService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
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
    @CrossOrigin(origins = "*")
    public static class RootController {
        
        @Autowired
        private MouseControlService mouseControlService;
        
        private final ObjectMapper objectMapper = new ObjectMapper();
        
        @GetMapping("/")
        public ResponseEntity<String> root() {
            return ResponseEntity.ok("Gesture Backend is running! Version: 2025-08-28-v2");
        }
        
        @GetMapping("/api")
        public ResponseEntity<String> apiRoot() {
            return ResponseEntity.ok("API is available!");
        }
        
        @GetMapping("/api/health")
        public ResponseEntity<String> health() {
            return ResponseEntity.ok("{\"status\": \"UP\", \"timestamp\": \"" + 
                java.time.LocalDateTime.now() + "\", \"service\": \"gesture-backend\", \"version\": \"0.1.0-unified\"}");
        }
        
        @GetMapping("/api/gesture/test")
        public ResponseEntity<String> gestureTest() {
            return ResponseEntity.ok("🎯 Gesture Controller is working!");
        }
        
        @PostMapping("/api/gesture/mouse/move")
        public ResponseEntity<String> mouseMove(@RequestBody String body) {
            try {
                System.out.println("🖱️ 마우스 이동 요청 받음: " + body);
                JsonNode json = objectMapper.readTree(body);
                int x = json.get("x").asInt();
                int y = json.get("y").asInt();
                
                mouseControlService.moveMouseSmooth(x, y);
                System.out.println("✅ 마우스 이동 완료: (" + x + ", " + y + ")");
                return ResponseEntity.ok("{\"success\": true, \"message\": \"마우스 이동 완료\"}");
            } catch (Exception e) {
                System.err.println("❌ 마우스 이동 오류: " + e.getMessage());
                return ResponseEntity.ok("{\"success\": false, \"message\": \"마우스 이동 실패\"}");
            }
        }
        
        @PostMapping("/api/gesture/mouse/left-click")
        public ResponseEntity<String> leftClick() {
            try {
                System.out.println("🖱️ 좌클릭 요청 받음");
                mouseControlService.leftClick();
                System.out.println("✅ 좌클릭 완료");
                return ResponseEntity.ok("{\"success\": true, \"message\": \"좌클릭 완료\"}");
            } catch (Exception e) {
                System.err.println("❌ 좌클릭 오류: " + e.getMessage());
                return ResponseEntity.ok("{\"success\": false, \"message\": \"좌클릭 실패\"}");
            }
        }
        
        @PostMapping("/api/gesture/mouse/right-click")
        public ResponseEntity<String> rightClick() {
            try {
                System.out.println("🖱️ 우클릭 요청 받음");
                mouseControlService.rightClick();
                System.out.println("✅ 우클릭 완료");
                return ResponseEntity.ok("{\"success\": true, \"message\": \"우클릭 완료\"}");
            } catch (Exception e) {
                System.err.println("❌ 우클릭 오류: " + e.getMessage());
                return ResponseEntity.ok("{\"success\": false, \"message\": \"우클릭 실패\"}");
            }
        }
        
        @PostMapping("/api/gesture/mouse/scroll")
        public ResponseEntity<String> scroll(@RequestBody String body) {
            try {
                System.out.println("📜 스크롤 요청 받음: " + body);
                JsonNode json = objectMapper.readTree(body);
                String direction = json.get("direction").asText();
                int amount = json.has("amount") ? json.get("amount").asInt() : 3;
                
                mouseControlService.scroll(direction, amount);
                System.out.println("✅ 스크롤 완료: " + direction + " (양: " + amount + ")");
                return ResponseEntity.ok("{\"success\": true, \"message\": \"스크롤 완료\"}");
            } catch (Exception e) {
                System.err.println("❌ 스크롤 오류: " + e.getMessage());
                return ResponseEntity.ok("{\"success\": false, \"message\": \"스크롤 실패\"}");
            }
        }
    }
}
