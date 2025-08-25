package com.gesture.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @GetMapping("/status")
    public ResponseEntity<String> getUserStatus() {
        return ResponseEntity.ok("User service is running");
    }
}
