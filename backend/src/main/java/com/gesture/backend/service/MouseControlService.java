package com.gesture.backend.service;

import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MouseControlService {
    
    private final Robot robot;
    private final Map<String, Long> lastActionTime = new ConcurrentHashMap<>();
    private final long ACTION_COOLDOWN = 100; // 100ms 쿨다운
    private final boolean isHeadless;
    
    // 마우스 위치 스무딩을 위한 변수들
    private int lastX = 0;
    private int lastY = 0;
    private final double SMOOTHING_FACTOR = 0.3;
    
    public MouseControlService() {
        // 헤드리스 환경 체크
        this.isHeadless = GraphicsEnvironment.isHeadless();
        
        if (isHeadless) {
            System.out.println("⚠️ 헤드리스 환경 감지 - GUI 기능 비활성화");
            this.robot = null;
        } else {
            try {
                // GUI 환경에서만 Robot 초기화
                this.robot = new Robot();
                robot.setAutoDelay(10); // 10ms 딜레이
                
                // 화면 크기 가져오기
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                System.out.println("🖥️ 화면 크기: " + screenSize.width + "x" + screenSize.height);
                System.out.println("✅ MouseControlService 초기화 완료");
            } catch (AWTException e) {
                System.err.println("❌ Robot 클래스 초기화 실패: " + e.getMessage());
                throw new RuntimeException("❌ Robot 클래스 초기화 실패: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * 마우스 위치 이동 (스무딩 적용)
     */
    public void moveMouseSmooth(int x, int y) {
        if (isHeadless || robot == null) {
            System.out.println("🖱️ 헤드리스 환경 - 마우스 이동 시뮬레이션: (" + x + ", " + y + ")");
            return;
        }
        
        try {
            // 스무딩 적용
            int smoothedX = (int) (lastX + (x - lastX) * SMOOTHING_FACTOR);
            int smoothedY = (int) (lastY + (y - lastY) * SMOOTHING_FACTOR);
            
            robot.mouseMove(smoothedX, smoothedY);
            
            lastX = smoothedX;
            lastY = smoothedY;
            
            System.out.println("🖱️ 마우스 이동: (" + smoothedX + ", " + smoothedY + ")");
        } catch (Exception e) {
            System.err.println("❌ 마우스 이동 실패: " + e.getMessage());
        }
    }
    
    /**
     * 마우스 위치 이동 (즉시)
     */
    public void moveMouse(int x, int y) {
        if (isHeadless || robot == null) {
            System.out.println("🖱️ 헤드리스 환경 - 마우스 이동 시뮬레이션: (" + x + ", " + y + ")");
            return;
        }
        
        try {
            robot.mouseMove(x, y);
            lastX = x;
            lastY = y;
            System.out.println("🖱️ 마우스 이동: (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.err.println("❌ 마우스 이동 실패: " + e.getMessage());
        }
    }
    
    /**
     * 좌클릭
     */
    public CompletableFuture<Void> leftClick() {
        if (isHeadless || robot == null) {
            System.out.println("🖱️ 헤드리스 환경 - 좌클릭 시뮬레이션");
            return CompletableFuture.completedFuture(null);
        }
        
        return performClickAction("leftClick", () -> {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(50);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            System.out.println("🖱️ 좌클릭 실행");
        });
    }
    
    /**
     * 우클릭
     */
    public CompletableFuture<Void> rightClick() {
        if (isHeadless || robot == null) {
            System.out.println("🖱️ 헤드리스 환경 - 우클릭 시뮬레이션");
            return CompletableFuture.completedFuture(null);
        }
        
        return performClickAction("rightClick", () -> {
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            robot.delay(50);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            System.out.println("🖱️ 우클릭 실행");
        });
    }
    
    /**
     * 더블클릭
     */
    public CompletableFuture<Void> doubleClick() {
        if (isHeadless || robot == null) {
            System.out.println("🖱️ 헤드리스 환경 - 더블클릭 시뮬레이션");
            return CompletableFuture.completedFuture(null);
        }
        
        return performClickAction("doubleClick", () -> {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(50);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(50);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            System.out.println("🖱️ 더블클릭 실행");
        });
    }
    
    /**
     * 스크롤
     */
    public void scroll(String direction, int amount) {
        if (isHeadless || robot == null) {
            System.out.println("📜 헤드리스 환경 - 스크롤 시뮬레이션: " + direction + " (양: " + amount + ")");
            return;
        }
        
        try {
            if (!canPerformAction("scroll")) return;
            
            int scrollAmount = direction.equals("up") ? -amount : amount;
            robot.mouseWheel(scrollAmount);
            
            recordAction("scroll");
            System.out.println("📜 스크롤: " + direction + " (양: " + amount + ")");
        } catch (Exception e) {
            System.err.println("❌ 스크롤 실패: " + e.getMessage());
        }
    }
    
    /**
     * 키보드 입력
     */
    public void pressKey(int keyCode) {
        if (isHeadless || robot == null) {
            System.out.println("⌨️ 헤드리스 환경 - 키 입력 시뮬레이션: " + KeyEvent.getKeyText(keyCode));
            return;
        }
        
        try {
            if (!canPerformAction("keyPress")) return;
            
            robot.keyPress(keyCode);
            robot.delay(50);
            robot.keyRelease(keyCode);
            
            recordAction("keyPress");
            System.out.println("⌨️ 키 입력: " + KeyEvent.getKeyText(keyCode));
        } catch (Exception e) {
            System.err.println("❌ 키 입력 실패: " + e.getMessage());
        }
    }
    
    /**
     * 키 조합 (Ctrl+C, Ctrl+V 등)
     */
    public void pressKeyCombo(int... keyCodes) {
        if (isHeadless || robot == null) {
            System.out.println("⌨️ 헤드리스 환경 - 키 조합 시뮬레이션");
            return;
        }
        
        try {
            if (!canPerformAction("keyCombo")) return;
            
            // 모든 키 누르기
            for (int keyCode : keyCodes) {
                robot.keyPress(keyCode);
                robot.delay(10);
            }
            
            robot.delay(50);
            
            // 역순으로 키 떼기
            for (int i = keyCodes.length - 1; i >= 0; i--) {
                robot.keyRelease(keyCodes[i]);
                robot.delay(10);
            }
            
            recordAction("keyCombo");
            System.out.println("⌨️ 키 조합 실행");
        } catch (Exception e) {
            System.err.println("❌ 키 조합 실패: " + e.getMessage());
        }
    }
    
    /**
     * 화면 해상도 가져오기
     */
    public Dimension getScreenSize() {
        if (isHeadless) {
            System.out.println("🖥️ 헤드리스 환경 - 기본 화면 크기 반환: 1920x1080");
            return new Dimension(1920, 1080);
        }
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
    
    /**
     * 현재 마우스 위치 가져오기
     */
    public Point getCurrentMousePosition() {
        if (isHeadless) {
            System.out.println("🖱️ 헤드리스 환경 - 기본 마우스 위치 반환: (0, 0)");
            return new Point(0, 0);
        }
        return MouseInfo.getPointerInfo().getLocation();
    }
    
    /**
     * 액션 실행 가능 여부 확인 (쿨다운)
     */
    private boolean canPerformAction(String actionType) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastActionTime.get(actionType);
        
        if (lastTime == null || currentTime - lastTime >= ACTION_COOLDOWN) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 액션 실행 시간 기록
     */
    private void recordAction(String actionType) {
        lastActionTime.put(actionType, System.currentTimeMillis());
    }
    
    /**
     * 클릭 액션 실행 (비동기)
     */
    private CompletableFuture<Void> performClickAction(String actionType, Runnable action) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!canPerformAction(actionType)) {
                    System.out.println("⏰ " + actionType + " 쿨다운 중...");
                    return;
                }
                
                action.run();
                recordAction(actionType);
            } catch (Exception e) {
                System.err.println("❌ " + actionType + " 실행 실패: " + e.getMessage());
            }
        });
    }
}
