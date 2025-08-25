package com.gesture.backend.controller;

import com.gesture.backend.dto.ProfileDto;
import com.gesture.backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileDto.Response>> getUserProfiles() {
        try {
            List<ProfileDto.Response> profiles = profileService.getUserProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Failed to get user profiles", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDto.Response> getProfileById(@PathVariable UUID profileId) {
        try {
            ProfileDto.Response profile = profileService.getProfileById(profileId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to get profile: {}", profileId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProfileDto.Response> createProfile(@RequestBody ProfileDto.CreateRequest request) {
        try {
            ProfileDto.Response profile = profileService.createProfile(request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to create profile", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileDto.Response> updateProfile(
            @PathVariable UUID profileId,
            @RequestBody ProfileDto.UpdateRequest request) {
        try {
            ProfileDto.Response profile = profileService.updateProfile(profileId, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to update profile: {}", profileId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete profile: {}", profileId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/default")
    public ResponseEntity<ProfileDto.Response> getDefaultProfile() {
        try {
            ProfileDto.Response profile = profileService.getDefaultProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Failed to get default profile", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
