package com.gesture.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class MappingDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private UUID profileId;
        private String name;
        private Map<String, Object> condition;
        private Map<String, Object> action;
        private Integer priority;
        private Boolean enabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private UUID id;
        private String name;
        private Map<String, Object> condition;
        private Map<String, Object> action;
        private Integer priority;
        private Boolean enabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private Map<String, Object> condition;
        private Map<String, Object> action;
        private Integer priority;
        private Boolean enabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private Map<String, Object> condition;
        private Map<String, Object> action;
        private Integer priority;
        private Boolean enabled;
    }
}
