package com.gesture.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @GetMapping("/status")
    public ResponseEntity<String> getAuthStatus() {
        return ResponseEntity.ok("Authentication service is running");
    }
}
