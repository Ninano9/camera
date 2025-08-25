package com.gesture.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String name;
        private String description;
        private Map<String, Object> context;
        private Boolean isDefault;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<MappingDto.Summary> mappings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private UUID id;
        private String name;
        private String description;
        private Boolean isDefault;
        private Boolean isActive;
        private Integer mappingCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private String description;
        private Map<String, Object> context;
        private Boolean isDefault;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String description;
        private Map<String, Object> context;
        private Boolean isDefault;
        private Boolean isActive;
    }
}
