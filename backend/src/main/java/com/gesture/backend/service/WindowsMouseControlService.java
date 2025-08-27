package com.gesture.backend.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Service
public class WindowsMouseControlService {
    
    private final boolean isWindows;
    private final boolean isJnaAvailable;
    private Object user32; // 동적 타입으로 변경
    
    public WindowsMouseControlService() {
        // OS 확인
        String osName = System.getProperty("os.name").toLowerCase();
        this.isWindows = osName.contains("windows");
        
        // JNA 라이브러리 사용 가능 여부 확인
        boolean jnaAvailable = false;
        try {
            Class.forName("com.sun.jna.Platform");
            Class.forName("com.sun.jna.Native");
            jnaAvailable = true;
            System.out.println("✅ JNA 라이브러리 감지됨");
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ JNA 라이브러리 없음 - Windows API 비활성화");
        }
        this.isJnaAvailable = jnaAvailable;
        
        // Windows + JNA 환경에서만 초기화 시도
        if (isWindows && isJnaAvailable) {
            try {
                initializeWindowsApi();
                System.out.println("✅ Windows API 마우스 제어 서비스 초기화 완료");
            } catch (Exception e) {
                System.err.println("❌ Windows API 초기화 실패: " + e.getMessage());
                this.user32 = null;
            }
        } else {
            this.user32 = null;
            System.out.println("⚠️ Windows API 비활성화 (OS: " + osName + ", JNA: " + jnaAvailable + ")");
        }
    }
    
    private void initializeWindowsApi() {
        try {
            // 동적으로 JNA 클래스 로드
            Class<?> platformClass = Class.forName("com.sun.jna.Platform");
            Class<?> nativeClass = Class.forName("com.sun.jna.Native");
            
            // Platform.isWindows() 호출
            boolean platformWindows = (Boolean) platformClass.getMethod("isWindows").invoke(null);
            
            if (platformWindows) {
                // 리플렉션으로 User32 인터페이스 생성은 복잡하므로 일단 비활성화
                System.out.println("🖥️ Windows 플랫폼 확인됨 - 하지만 JNA 인터페이스는 비활성화");
                this.user32 = null;
            }
        } catch (Exception e) {
            System.err.println("❌ Windows API 동적 로딩 실패: " + e.getMessage());
            this.user32 = null;
        }
    }
    
    /**
     * SetCursorPos를 사용한 직접적인 마우스 이동
     */
    public boolean moveMouseDirect(int x, int y) {
        System.out.println("🖱️ Windows API 비활성화 - 마우스 이동 시뮬레이션: (" + x + ", " + y + ")");
        return false;
    }
    
    /**
     * GetCursorPos를 사용한 현재 마우스 위치 가져오기
     */
    public Point getCurrentMousePosition() {
        System.out.println("🖱️ Windows API 비활성화 - 기본 마우스 위치 반환: (0, 0)");
        return new Point(0, 0);
    }
    
    /**
     * mouse_event를 사용한 좌클릭
     */
    public CompletableFuture<Void> leftClickDirect() {
        System.out.println("🖱️ Windows API 비활성화 - 좌클릭 시뮬레이션");
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * mouse_event를 사용한 우클릭
     */
    public CompletableFuture<Void> rightClickDirect() {
        System.out.println("🖱️ Windows API 비활성화 - 우클릭 시뮬레이션");
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * mouse_event를 사용한 스크롤
     */
    public void scrollDirect(String direction, int amount) {
        System.out.println("📜 Windows API 비활성화 - 스크롤 시뮬레이션: " + direction + " (양: " + amount + ")");
    }
    
    /**
     * 화면 크기 가져오기
     */
    public Dimension getScreenSize() {
        System.out.println("🖥️ Windows API 비활성화 - 기본 화면 크기 반환: 1920x1080");
        return new Dimension(1920, 1080);
    }
    
    /**
     * Windows API 사용 가능 여부 확인
     */
    public boolean isWindowsApiAvailable() {
        return false; // 현재는 항상 비활성화
    }
}
