package com.gesture.backend.controller;

import com.gesture.backend.dto.GestureDto;
import com.gesture.backend.service.MouseControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/gesture")
@CrossOrigin(
    origins = {
        "https://camera-frontend-0gzf.onrender.com",
        "http://localhost:3000",
        "http://localhost:5173"
    },
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = true
)
@Validated
public class GestureController {
    
    @Autowired
    private MouseControlService mouseControlService;
    
    /**
     * 테스트 엔드포인트
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Gesture Controller is working!");
    }
    
    /**
     * 마우스 이동
     */
    @PostMapping("/mouse/move")
    public ResponseEntity<GestureDto.Response> moveMouse(@Valid @RequestBody GestureDto.CommandRequest request) {
        try {
            if (request.getX() == null || request.getY() == null) {
                return ResponseEntity.badRequest()
                    .body(new GestureDto.Response(false, "x, y 좌표가 필요합니다", null));
            }
            
            mouseControlService.moveMouseSmooth(request.getX(), request.getY());
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, "마우스 이동 완료", "mouse_move")
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "마우스 이동 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 좌클릭
     */
    @PostMapping("/mouse/left-click")
    public ResponseEntity<GestureDto.Response> leftClick() {
        try {
            CompletableFuture<Void> future = mouseControlService.leftClick();
            future.get(); // 완료까지 대기
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, "좌클릭 실행 완료", "left_click")
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "좌클릭 실행 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 우클릭
     */
    @PostMapping("/mouse/right-click")
    public ResponseEntity<GestureDto.Response> rightClick() {
        try {
            CompletableFuture<Void> future = mouseControlService.rightClick();
            future.get(); // 완료까지 대기
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, "우클릭 실행 완료", "right_click")
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "우클릭 실행 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 더블클릭
     */
    @PostMapping("/mouse/double-click")
    public ResponseEntity<GestureDto.Response> doubleClick() {
        try {
            CompletableFuture<Void> future = mouseControlService.doubleClick();
            future.get(); // 완료까지 대기
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, "더블클릭 실행 완료", "double_click")
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "더블클릭 실행 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 스크롤
     */
    @PostMapping("/mouse/scroll")
    public ResponseEntity<GestureDto.Response> scroll(@Valid @RequestBody GestureDto.CommandRequest request) {
        try {
            String direction = request.getDirection();
            Integer amount = request.getAmount();
            
            if (direction == null || (!direction.equals("up") && !direction.equals("down"))) {
                return ResponseEntity.badRequest()
                    .body(new GestureDto.Response(false, "direction은 'up' 또는 'down'이어야 합니다", null));
            }
            
            if (amount == null || amount <= 0) {
                amount = 3; // 기본값
            }
            
            mouseControlService.scroll(direction, amount);
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, direction + " 스크롤 실행 완료", "scroll_" + direction)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "스크롤 실행 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 키보드 입력
     */
    @PostMapping("/keyboard/key")
    public ResponseEntity<GestureDto.Response> pressKey(@RequestParam String key) {
        try {
            int keyCode = getKeyCode(key.toUpperCase());
            if (keyCode == -1) {
                return ResponseEntity.badRequest()
                    .body(new GestureDto.Response(false, "지원하지 않는 키: " + key, null));
            }
            
            mouseControlService.pressKey(keyCode);
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, key + " 키 입력 완료", "key_" + key.toLowerCase())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "키 입력 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 키 조합 (예: Ctrl+C, Ctrl+V)
     */
    @PostMapping("/keyboard/combo")
    public ResponseEntity<GestureDto.Response> pressKeyCombo(@RequestParam String combo) {
        try {
            int[] keyCodes = parseKeyCombo(combo);
            if (keyCodes.length == 0) {
                return ResponseEntity.badRequest()
                    .body(new GestureDto.Response(false, "잘못된 키 조합: " + combo, null));
            }
            
            mouseControlService.pressKeyCombo(keyCodes);
            
            return ResponseEntity.ok(
                new GestureDto.Response(true, combo + " 키 조합 실행 완료", "combo_" + combo.toLowerCase())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "키 조합 실행 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 화면 정보 가져오기
     */
    @GetMapping("/screen/info")
    public ResponseEntity<GestureDto.ScreenInfo> getScreenInfo() {
        try {
            Dimension screenSize = mouseControlService.getScreenSize();
            Point mousePosition = mouseControlService.getCurrentMousePosition();
            
            GestureDto.ScreenInfo screenInfo = new GestureDto.ScreenInfo(
                screenSize.width,
                screenSize.height,
                mousePosition.x,
                mousePosition.y
            );
            
            return ResponseEntity.ok(screenInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 통합 제스처 처리 (프론트엔드에서 분석된 제스처 실행)
     */
    @PostMapping("/execute")
    public ResponseEntity<GestureDto.Response> executeGesture(@Valid @RequestBody GestureDto.CommandRequest request) {
        try {
            String gestureType = request.getGestureType().toLowerCase();
            
            switch (gestureType) {
                case "mouse_move":
                    if (request.getX() != null && request.getY() != null) {
                        mouseControlService.moveMouseSmooth(request.getX(), request.getY());
                        return ResponseEntity.ok(
                            new GestureDto.Response(true, "마우스 이동 실행", gestureType)
                        );
                    }
                    break;
                    
                case "left_click":
                    mouseControlService.leftClick().get();
                    return ResponseEntity.ok(
                        new GestureDto.Response(true, "좌클릭 실행", gestureType)
                    );
                    
                case "right_click":
                    mouseControlService.rightClick().get();
                    return ResponseEntity.ok(
                        new GestureDto.Response(true, "우클릭 실행", gestureType)
                    );
                    
                case "scroll_up":
                    mouseControlService.scroll("up", request.getAmount() != null ? request.getAmount() : 3);
                    return ResponseEntity.ok(
                        new GestureDto.Response(true, "위로 스크롤 실행", gestureType)
                    );
                    
                case "scroll_down":
                    mouseControlService.scroll("down", request.getAmount() != null ? request.getAmount() : 3);
                    return ResponseEntity.ok(
                        new GestureDto.Response(true, "아래로 스크롤 실행", gestureType)
                    );
                    
                case "escape":
                    mouseControlService.pressKey(KeyEvent.VK_ESCAPE);
                    return ResponseEntity.ok(
                        new GestureDto.Response(true, "ESC 키 실행", gestureType)
                    );
                    
                default:
                    return ResponseEntity.badRequest()
                        .body(new GestureDto.Response(false, "지원하지 않는 제스처: " + gestureType, null));
            }
            
            return ResponseEntity.badRequest()
                .body(new GestureDto.Response(false, "제스처 실행에 필요한 파라미터가 부족합니다", null));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GestureDto.Response(false, "제스처 실행 실패: " + e.getMessage(), null));
        }
    }
    
    /**
     * 키 문자열을 KeyEvent 상수로 변환
     */
    private int getKeyCode(String key) {
        switch (key) {
            case "ESCAPE": case "ESC": return KeyEvent.VK_ESCAPE;
            case "ENTER": return KeyEvent.VK_ENTER;
            case "SPACE": return KeyEvent.VK_SPACE;
            case "TAB": return KeyEvent.VK_TAB;
            case "BACKSPACE": return KeyEvent.VK_BACK_SPACE;
            case "DELETE": return KeyEvent.VK_DELETE;
            case "UP": return KeyEvent.VK_UP;
            case "DOWN": return KeyEvent.VK_DOWN;
            case "LEFT": return KeyEvent.VK_LEFT;
            case "RIGHT": return KeyEvent.VK_RIGHT;
            case "CTRL": return KeyEvent.VK_CONTROL;
            case "ALT": return KeyEvent.VK_ALT;
            case "SHIFT": return KeyEvent.VK_SHIFT;
            case "F1": return KeyEvent.VK_F1;
            case "F2": return KeyEvent.VK_F2;
            case "F3": return KeyEvent.VK_F3;
            case "F4": return KeyEvent.VK_F4;
            case "F5": return KeyEvent.VK_F5;
            default:
                if (key.length() == 1) {
                    char c = key.charAt(0);
                    if (c >= 'A' && c <= 'Z') return KeyEvent.VK_A + (c - 'A');
                    if (c >= '0' && c <= '9') return KeyEvent.VK_0 + (c - '0');
                }
                return -1;
        }
    }
    
    /**
     * 키 조합 문자열 파싱 (예: "CTRL+C" -> [VK_CONTROL, VK_C])
     */
    private int[] parseKeyCombo(String combo) {
        String[] parts = combo.toUpperCase().split("\\+");
        int[] keyCodes = new int[parts.length];
        
        for (int i = 0; i < parts.length; i++) {
            int keyCode = getKeyCode(parts[i].trim());
            if (keyCode == -1) {
                return new int[0]; // 잘못된 키 조합
            }
            keyCodes[i] = keyCode;
        }
        
        return keyCodes;
    }
}
