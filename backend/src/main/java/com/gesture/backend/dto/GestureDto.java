package com.gesture.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

public class GestureDto {
    
    /**
     * 제스처 명령 요청 DTO
     */
    public static class CommandRequest {
        @NotBlank(message = "제스처 타입은 필수입니다")
        private String gestureType;
        
        private Integer x;
        private Integer y;
        private String direction;
        private Integer amount;
        private List<HandLandmark> landmarks;
        
        // Getters and Setters
        public String getGestureType() {
            return gestureType;
        }
        
        public void setGestureType(String gestureType) {
            this.gestureType = gestureType;
        }
        
        public Integer getX() {
            return x;
        }
        
        public void setX(Integer x) {
            this.x = x;
        }
        
        public Integer getY() {
            return y;
        }
        
        public void setY(Integer y) {
            this.y = y;
        }
        
        public String getDirection() {
            return direction;
        }
        
        public void setDirection(String direction) {
            this.direction = direction;
        }
        
        public Integer getAmount() {
            return amount;
        }
        
        public void setAmount(Integer amount) {
            this.amount = amount;
        }
        
        public List<HandLandmark> getLandmarks() {
            return landmarks;
        }
        
        public void setLandmarks(List<HandLandmark> landmarks) {
            this.landmarks = landmarks;
        }
    }
    
    /**
     * 손 랜드마크 포인트
     */
    public static class HandLandmark {
        @NotNull(message = "x 좌표는 필수입니다")
        @Min(value = 0, message = "x 좌표는 0 이상이어야 합니다")
        @Max(value = 1, message = "x 좌표는 1 이하여야 합니다")
        private Double x;
        
        @NotNull(message = "y 좌표는 필수입니다")
        @Min(value = 0, message = "y 좌표는 0 이상이어야 합니다")
        @Max(value = 1, message = "y 좌표는 1 이하여야 합니다")
        private Double y;
        
        private Double z;
        private Double visibility;
        
        // 기본 생성자
        public HandLandmark() {}
        
        // 생성자
        public HandLandmark(Double x, Double y, Double z, Double visibility) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.visibility = visibility;
        }
        
        // Getters and Setters
        public Double getX() {
            return x;
        }
        
        public void setX(Double x) {
            this.x = x;
        }
        
        public Double getY() {
            return y;
        }
        
        public void setY(Double y) {
            this.y = y;
        }
        
        public Double getZ() {
            return z;
        }
        
        public void setZ(Double z) {
            this.z = z;
        }
        
        public Double getVisibility() {
            return visibility;
        }
        
        public void setVisibility(Double visibility) {
            this.visibility = visibility;
        }
    }
    
    /**
     * 제스처 응답 DTO
     */
    public static class Response {
        private boolean success;
        private String message;
        private String executedAction;
        private Long timestamp;
        
        // 기본 생성자
        public Response() {}
        
        // 생성자
        public Response(boolean success, String message, String executedAction) {
            this.success = success;
            this.message = message;
            this.executedAction = executedAction;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getExecutedAction() {
            return executedAction;
        }
        
        public void setExecutedAction(String executedAction) {
            this.executedAction = executedAction;
        }
        
        public Long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    /**
     * 화면 정보 응답 DTO
     */
    public static class ScreenInfo {
        private int width;
        private int height;
        private int currentMouseX;
        private int currentMouseY;
        
        // 기본 생성자
        public ScreenInfo() {}
        
        // 생성자
        public ScreenInfo(int width, int height, int currentMouseX, int currentMouseY) {
            this.width = width;
            this.height = height;
            this.currentMouseX = currentMouseX;
            this.currentMouseY = currentMouseY;
        }
        
        // Getters and Setters
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public int getCurrentMouseX() {
            return currentMouseX;
        }
        
        public void setCurrentMouseX(int currentMouseX) {
            this.currentMouseX = currentMouseX;
        }
        
        public int getCurrentMouseY() {
            return currentMouseY;
        }
        
        public void setCurrentMouseY(int currentMouseY) {
            this.currentMouseY = currentMouseY;
        }
    }
}
