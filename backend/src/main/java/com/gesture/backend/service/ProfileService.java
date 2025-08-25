package com.gesture.backend.service;

import com.gesture.backend.dto.MappingDto;
import com.gesture.backend.dto.ProfileDto;
import com.gesture.backend.entity.Profile;
import com.gesture.backend.entity.User;
import com.gesture.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ProfileDto.Response> getUserProfiles() {
        User currentUser = userService.getCurrentUserEntity();
        List<Profile> profiles = profileRepository.findByUserIdAndIsActiveTrue(currentUser.getId());
        
        return profiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProfileDto.Response getProfileById(UUID profileId) {
        Profile profile = profileRepository.findByIdWithMappings(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 현재 사용자의 프로파일인지 확인
        User currentUser = userService.getCurrentUserEntity();
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToDtoWithMappings(profile);
    }

    @Transactional
    public ProfileDto.Response createProfile(ProfileDto.CreateRequest request) {
        User currentUser = userService.getCurrentUserEntity();

        // 이름 중복 체크
        if (profileRepository.existsByUserIdAndNameAndIsActiveTrue(currentUser.getId(), request.getName())) {
            throw new RuntimeException("Profile name already exists: " + request.getName());
        }

        // 기본 프로파일로 설정하는 경우 기존 기본 프로파일 해제
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            setAllProfilesNonDefault(currentUser.getId());
        }

        Profile profile = Profile.builder()
                .user(currentUser)
                .name(request.getName())
                .description(request.getDescription())
                .context(request.getContext())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .isActive(true)
                .build();

        Profile savedProfile = profileRepository.save(profile);
        log.info("Profile created: {} for user: {}", savedProfile.getName(), currentUser.getEmail());

        return convertToDto(savedProfile);
    }

    @Transactional
    public ProfileDto.Response updateProfile(UUID profileId, ProfileDto.UpdateRequest request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 현재 사용자의 프로파일인지 확인
        User currentUser = userService.getCurrentUserEntity();
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // 이름 변경 시 중복 체크
        if (request.getName() != null && !request.getName().equals(profile.getName())) {
            if (profileRepository.existsByUserIdAndNameAndIsActiveTrue(currentUser.getId(), request.getName())) {
                throw new RuntimeException("Profile name already exists: " + request.getName());
            }
            profile.setName(request.getName());
        }

        if (request.getDescription() != null) {
            profile.setDescription(request.getDescription());
        }
        if (request.getContext() != null) {
            profile.setContext(request.getContext());
        }
        if (request.getIsActive() != null) {
            profile.setIsActive(request.getIsActive());
        }

        // 기본 프로파일로 설정하는 경우 기존 기본 프로파일 해제
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(profile.getIsDefault())) {
            setAllProfilesNonDefault(currentUser.getId());
            profile.setIsDefault(true);
        } else if (Boolean.FALSE.equals(request.getIsDefault())) {
            profile.setIsDefault(false);
        }

        Profile savedProfile = profileRepository.save(profile);
        log.info("Profile updated: {} for user: {}", savedProfile.getName(), currentUser.getEmail());

        return convertToDto(savedProfile);
    }

    @Transactional
    public void deleteProfile(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // 현재 사용자의 프로파일인지 확인
        User currentUser = userService.getCurrentUserEntity();
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // 기본 프로파일은 삭제할 수 없음
        if (Boolean.TRUE.equals(profile.getIsDefault())) {
            throw new RuntimeException("Cannot delete default profile");
        }

        profile.setIsActive(false);
        profileRepository.save(profile);
        log.info("Profile deleted: {} for user: {}", profile.getName(), currentUser.getEmail());
    }

    @Transactional(readOnly = true)
    public ProfileDto.Response getDefaultProfile() {
        User currentUser = userService.getCurrentUserEntity();
        Profile defaultProfile = profileRepository.findDefaultProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Default profile not found"));

        return convertToDto(defaultProfile);
    }

    private void setAllProfilesNonDefault(UUID userId) {
        List<Profile> profiles = profileRepository.findByUserIdAndIsActiveTrue(userId);
        profiles.forEach(profile -> profile.setIsDefault(false));
        profileRepository.saveAll(profiles);
    }

    private ProfileDto.Response convertToDto(Profile profile) {
        return ProfileDto.Response.builder()
                .id(profile.getId())
                .name(profile.getName())
                .description(profile.getDescription())
                .context(profile.getContext())
                .isDefault(profile.getIsDefault())
                .isActive(profile.getIsActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private ProfileDto.Response convertToDtoWithMappings(Profile profile) {
        List<MappingDto.Summary> mappingSummaries = profile.getMappings().stream()
                .filter(mapping -> mapping.getEnabled())
                .map(mapping -> MappingDto.Summary.builder()
                        .id(mapping.getId())
                        .name(mapping.getName())
                        .condition(mapping.getCondition())
                        .action(mapping.getAction())
                        .priority(mapping.getPriority())
                        .enabled(mapping.getEnabled())
                        .build())
                .collect(Collectors.toList());

        return ProfileDto.Response.builder()
                .id(profile.getId())
                .name(profile.getName())
                .description(profile.getDescription())
                .context(profile.getContext())
                .isDefault(profile.getIsDefault())
                .isActive(profile.getIsActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .mappings(mappingSummaries)
                .build();
    }
}
