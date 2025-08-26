package com.gesture.backend.controller;

import com.gesture.backend.dto.GestureDto;
import com.gesture.backend.service.MouseControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.awt.event.KeyEvent;
import java.util.concurrent.CompletableFuture;

@Controller
public class GestureWebSocketController {
    
    @Autowired
    private MouseControlService mouseControlService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 실시간 마우스 이동 처리
     */
    @MessageMapping("/gesture/mouse/move")
    public void handleMouseMove(GestureDto.CommandRequest request) {
        try {
            if (request.getX() != null && request.getY() != null) {
                // 부드러운 마우스 이동
                mouseControlService.moveMouseSmooth(request.getX(), request.getY());
                
                // 클라이언트에게 성공 응답
                GestureDto.Response response = new GestureDto.Response(
                    true, 
                    "마우스 이동 완료", 
                    "mouse_move"
                );
                
                messagingTemplate.convertAndSend("/topic/gesture/response", response);
            }
        } catch (Exception e) {
            sendErrorResponse("마우스 이동 실패: " + e.getMessage());
        }
    }
    
    /**
     * 실시간 제스처 실행 처리
     */
    @MessageMapping("/gesture/execute")
    @SendTo("/topic/gesture/response")
    public GestureDto.Response handleGestureExecution(GestureDto.CommandRequest request) {
        try {
            String gestureType = request.getGestureType().toLowerCase();
            
            switch (gestureType) {
                case "mouse_pointer":
                    // 마우스 포인터 이동 (실시간)
                    if (request.getX() != null && request.getY() != null) {
                        mouseControlService.moveMouseSmooth(request.getX(), request.getY());
                        return new GestureDto.Response(true, "마우스 포인터 이동", gestureType);
                    }
                    break;
                    
                case "left_click":
                    // 좌클릭 실행
                    CompletableFuture<Void> leftClickFuture = mouseControlService.leftClick();
                    leftClickFuture.get(); // 완료까지 대기
                    return new GestureDto.Response(true, "좌클릭 실행 완료", gestureType);
                    
                case "right_click":
                    // 우클릭 실행
                    CompletableFuture<Void> rightClickFuture = mouseControlService.rightClick();
                    rightClickFuture.get(); // 완료까지 대기
                    return new GestureDto.Response(true, "우클릭 실행 완료", gestureType);
                    
                case "scroll_up":
                    // 위로 스크롤
                    int upAmount = request.getAmount() != null ? request.getAmount() : 3;
                    mouseControlService.scroll("up", upAmount);
                    return new GestureDto.Response(true, "위로 스크롤 실행", gestureType);
                    
                case "scroll_down":
                    // 아래로 스크롤
                    int downAmount = request.getAmount() != null ? request.getAmount() : 3;
                    mouseControlService.scroll("down", downAmount);
                    return new GestureDto.Response(true, "아래로 스크롤 실행", gestureType);
                    
                case "escape":
                    // ESC 키 실행
                    mouseControlService.pressKey(KeyEvent.VK_ESCAPE);
                    return new GestureDto.Response(true, "ESC 키 실행 완료", gestureType);
                    
                case "double_click":
                    // 더블클릭 실행
                    CompletableFuture<Void> doubleClickFuture = mouseControlService.doubleClick();
                    doubleClickFuture.get(); // 완료까지 대기
                    return new GestureDto.Response(true, "더블클릭 실행 완료", gestureType);
                    
                default:
                    return new GestureDto.Response(false, "지원하지 않는 제스처: " + gestureType, null);
            }
            
            return new GestureDto.Response(false, "제스처 실행에 필요한 파라미터가 부족합니다", null);
            
        } catch (Exception e) {
            return new GestureDto.Response(false, "제스처 실행 실패: " + e.getMessage(), null);
        }
    }
    
    /**
     * 손 랜드마크 데이터 수신 및 분석
     */
    @MessageMapping("/gesture/landmarks")
    @SendTo("/topic/gesture/analysis")
    public GestureDto.Response analyzeLandmarks(GestureDto.CommandRequest request) {
        try {
            if (request.getLandmarks() == null || request.getLandmarks().isEmpty()) {
                return new GestureDto.Response(false, "손 랜드마크 데이터가 없습니다", null);
            }
            
            // 백엔드에서 추가적인 제스처 분석을 수행할 수 있음
            // 현재는 프론트엔드에서 분석하므로 간단한 로깅만
            System.out.println("📊 손 랜드마크 데이터 수신: " + request.getLandmarks().size() + "개 포인트");
            
            return new GestureDto.Response(true, "랜드마크 데이터 분석 완료", "landmarks_analysis");
            
        } catch (Exception e) {
            return new GestureDto.Response(false, "랜드마크 분석 실패: " + e.getMessage(), null);
        }
    }
    
    /**
     * 시스템 상태 확인
     */
    @MessageMapping("/gesture/system/status")
    @SendTo("/topic/gesture/system")
    public GestureDto.ScreenInfo getSystemStatus() {
        try {
            var screenSize = mouseControlService.getScreenSize();
            var mousePosition = mouseControlService.getCurrentMousePosition();
            
            return new GestureDto.ScreenInfo(
                screenSize.width,
                screenSize.height,
                mousePosition.x,
                mousePosition.y
            );
        } catch (Exception e) {
            System.err.println("시스템 상태 확인 실패: " + e.getMessage());
            return new GestureDto.ScreenInfo(0, 0, 0, 0);
        }
    }
    
    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(String message) {
        GestureDto.Response errorResponse = new GestureDto.Response(false, message, null);
        messagingTemplate.convertAndSend("/topic/gesture/error", errorResponse);
    }
}
