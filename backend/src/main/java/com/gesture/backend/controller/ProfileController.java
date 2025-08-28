package com.gesture.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@Slf4j
public class ProfileController {

    @GetMapping("/status")
    public ResponseEntity<String> getProfileStatus() {
        return ResponseEntity.ok("Profile service is running");
    }
}