package com.gesture.backend.service;

import com.gesture.backend.dto.ProfileDto;
import com.gesture.backend.dto.UserDto;
import com.gesture.backend.entity.User;
import com.gesture.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDto.Response getCurrentUser() {
        User user = getCurrentUserEntity();
        return convertToDto(user);
    }

    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findActiveUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDto.Response updateUser(UUID userId, UserDto.UpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 현재 사용자만 자신의 정보를 수정할 수 있도록 체크
        User currentUser = getCurrentUserEntity();
        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        User savedUser = userRepository.save(user);
        log.info("User updated: {}", savedUser.getEmail());

        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto.Response getUserById(UUID userId) {
        User user = userRepository.findByIdWithProfiles(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDtoWithProfiles(user);
    }

    public UserDto.Response convertToDto(User user) {
        return UserDto.Response.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserDto.Response convertToDtoWithProfiles(User user) {
        List<ProfileDto.Summary> profileSummaries = user.getProfiles().stream()
                .filter(profile -> profile.getIsActive())
                .map(profile -> ProfileDto.Summary.builder()
                        .id(profile.getId())
                        .name(profile.getName())
                        .description(profile.getDescription())
                        .isDefault(profile.getIsDefault())
                        .isActive(profile.getIsActive())
                        .mappingCount(profile.getMappings().size())
                        .build())
                .collect(Collectors.toList());

        return UserDto.Response.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .profiles(profileSummaries)
                .build();
    }
}
