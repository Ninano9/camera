package com.gesture.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
public class GestureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestureBackendApplication.class, args);
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
