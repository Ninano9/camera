package com.gesture.backend.service;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class WindowsMouseControlService {
    
    // Windows API 인터페이스 정의
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        
        // SetCursorPos - 마우스 커서 위치 설정
        boolean SetCursorPos(int x, int y);
        
        // GetCursorPos - 현재 마우스 커서 위치 가져오기
        boolean GetCursorPos(POINT point);
        
        // mouse_event - 마우스 이벤트 발생
        void mouse_event(int dwFlags, int dx, int dy, int dwData, int dwExtraInfo);
        
        // GetSystemMetrics - 시스템 정보 가져오기
        int GetSystemMetrics(int nIndex);
        
        // 마우스 이벤트 플래그 상수
        int MOUSEEVENTF_LEFTDOWN = 0x0002;
        int MOUSEEVENTF_LEFTUP = 0x0004;
        int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        int MOUSEEVENTF_RIGHTUP = 0x0010;
        int MOUSEEVENTF_WHEEL = 0x0800;
        
        // 시스템 메트릭스 상수
        int SM_CXSCREEN = 0;
        int SM_CYSCREEN = 1;
    }
    
    // POINT 구조체 정의
    public static class POINT extends Structure {
        public int x, y;
        
        public POINT() {
        }
        
        public POINT(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x", "y");
        }
    }
    
    private final boolean isWindows;
    private final User32 user32;
    
    public WindowsMouseControlService() {
        this.isWindows = Platform.isWindows();
        
        if (isWindows) {
            try {
                this.user32 = User32.INSTANCE;
                System.out.println("✅ Windows API 마우스 제어 서비스 초기화 완료");
                
                // 현재 화면 크기 출력
                int screenWidth = user32.GetSystemMetrics(User32.SM_CXSCREEN);
                int screenHeight = user32.GetSystemMetrics(User32.SM_CYSCREEN);
                System.out.println("🖥️ Windows 화면 크기: " + screenWidth + "x" + screenHeight);
                
            } catch (Exception e) {
                System.err.println("❌ Windows API 초기화 실패: " + e.getMessage());
                throw new RuntimeException("Windows API 초기화 실패", e);
            }
        } else {
            this.user32 = null;
            System.out.println("⚠️ Windows가 아닌 환경 - Windows API 비활성화");
        }
    }
    
    /**
     * SetCursorPos를 사용한 직접적인 마우스 이동
     */
    public boolean moveMouseDirect(int x, int y) {
        if (!isWindows || user32 == null) {
            System.out.println("🖱️ Windows API 비활성화 - 마우스 이동 시뮬레이션: (" + x + ", " + y + ")");
            return false;
        }
        
        try {
            boolean success = user32.SetCursorPos(x, y);
            if (success) {
                System.out.println("✅ Windows API 마우스 이동 성공: (" + x + ", " + y + ")");
            } else {
                System.err.println("❌ Windows API 마우스 이동 실패: (" + x + ", " + y + ")");
            }
            return success;
        } catch (Exception e) {
            System.err.println("❌ SetCursorPos 호출 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * GetCursorPos를 사용한 현재 마우스 위치 가져오기
     */
    public Point getCurrentMousePosition() {
        if (!isWindows || user32 == null) {
            System.out.println("🖱️ Windows API 비활성화 - 기본 마우스 위치 반환: (0, 0)");
            return new Point(0, 0);
        }
        
        try {
            POINT point = new POINT();
            boolean success = user32.GetCursorPos(point);
            
            if (success) {
                System.out.println("📍 현재 마우스 위치: (" + point.x + ", " + point.y + ")");
                return new Point(point.x, point.y);
            } else {
                System.err.println("❌ GetCursorPos 호출 실패");
                return new Point(0, 0);
            }
        } catch (Exception e) {
            System.err.println("❌ GetCursorPos 호출 오류: " + e.getMessage());
            return new Point(0, 0);
        }
    }
    
    /**
     * mouse_event를 사용한 좌클릭
     */
    public CompletableFuture<Void> leftClickDirect() {
        if (!isWindows || user32 == null) {
            System.out.println("🖱️ Windows API 비활성화 - 좌클릭 시뮬레이션");
            return CompletableFuture.completedFuture(null);
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                // 마우스 왼쪽 버튼 누르기
                user32.mouse_event(User32.MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
                Thread.sleep(50); // 50ms 대기
                // 마우스 왼쪽 버튼 떼기
                user32.mouse_event(User32.MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
                
                System.out.println("✅ Windows API 좌클릭 실행 완료");
            } catch (Exception e) {
                System.err.println("❌ Windows API 좌클릭 실패: " + e.getMessage());
            }
        });
    }
    
    /**
     * mouse_event를 사용한 우클릭
     */
    public CompletableFuture<Void> rightClickDirect() {
        if (!isWindows || user32 == null) {
            System.out.println("🖱️ Windows API 비활성화 - 우클릭 시뮬레이션");
            return CompletableFuture.completedFuture(null);
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                // 마우스 오른쪽 버튼 누르기
                user32.mouse_event(User32.MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
                Thread.sleep(50); // 50ms 대기
                // 마우스 오른쪽 버튼 떼기
                user32.mouse_event(User32.MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
                
                System.out.println("✅ Windows API 우클릭 실행 완료");
            } catch (Exception e) {
                System.err.println("❌ Windows API 우클릭 실패: " + e.getMessage());
            }
        });
    }
    
    /**
     * mouse_event를 사용한 스크롤
     */
    public void scrollDirect(String direction, int amount) {
        if (!isWindows || user32 == null) {
            System.out.println("📜 Windows API 비활성화 - 스크롤 시뮬레이션: " + direction + " (양: " + amount + ")");
            return;
        }
        
        try {
            // Windows에서 스크롤 방향: 음수는 아래로, 양수는 위로
            int wheelDelta = direction.equals("up") ? (amount * 120) : -(amount * 120);
            
            user32.mouse_event(User32.MOUSEEVENTF_WHEEL, 0, 0, wheelDelta, 0);
            
            System.out.println("✅ Windows API 스크롤 실행: " + direction + " (양: " + amount + ")");
        } catch (Exception e) {
            System.err.println("❌ Windows API 스크롤 실패: " + e.getMessage());
        }
    }
    
    /**
     * 화면 크기 가져오기
     */
    public Dimension getScreenSize() {
        if (!isWindows || user32 == null) {
            System.out.println("🖥️ Windows API 비활성화 - 기본 화면 크기 반환: 1920x1080");
            return new Dimension(1920, 1080);
        }
        
        try {
            int width = user32.GetSystemMetrics(User32.SM_CXSCREEN);
            int height = user32.GetSystemMetrics(User32.SM_CYSCREEN);
            
            System.out.println("🖥️ Windows API 화면 크기: " + width + "x" + height);
            return new Dimension(width, height);
        } catch (Exception e) {
            System.err.println("❌ 화면 크기 가져오기 실패: " + e.getMessage());
            return new Dimension(1920, 1080);
        }
    }
    
    /**
     * Windows API 사용 가능 여부 확인
     */
    public boolean isWindowsApiAvailable() {
        return isWindows && user32 != null;
    }
}
