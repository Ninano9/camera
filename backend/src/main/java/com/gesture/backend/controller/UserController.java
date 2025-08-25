package com.gesture.backend.controller;

import com.gesture.backend.dto.UserDto;
import com.gesture.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        try {
            UserDto.Response response = userService.getCurrentUser();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto.Response> updateCurrentUser(@RequestBody UserDto.UpdateRequest request) {
        try {
            UserDto.Response currentUser = userService.getCurrentUser();
            UserDto.Response response = userService.updateUser(currentUser.getId(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update current user", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable UUID userId) {
        try {
            UserDto.Response response = userService.getUserById(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user by id: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserDto.UpdateRequest request) {
        try {
            UserDto.Response response = userService.updateUser(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update user: {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
