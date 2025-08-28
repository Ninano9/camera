package com.gesture.backend.service;

import org.springframework.stereotype.Service;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Service  
public class WindowsMouseControlService {
    
    // Simplified version without JNA for deployment stability
    private final boolean isWindows;
    private final boolean isWindowsApiAvailable = false; // Temporarily disabled
    
    public WindowsMouseControlService() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        this.isWindows = osName.contains("windows");
        
        System.out.println("🖥️ WindowsMouseControlService 초기화");
        System.out.println("  - OS: " + osName);
        System.out.println("  - Windows: " + isWindows);
        System.out.println("  - Windows API: 임시 비활성화 (배포 안정성)");
        System.out.println("  - 대체: Java Robot 클래스 사용");
    }
    
    /**
     * Windows API 사용 가능 여부 (현재 비활성화)
     */
    public boolean isWindowsApiAvailable() {
        return false; // Temporarily disabled for deployment stability
    }
    
    /**
     * 마우스 이동 (시뮬레이션)
     */
    public boolean moveMouseDirect(int x, int y) {
        System.out.println("🔄 마우스 이동 요청 (시뮬레이션): (" + x + ", " + y + ")");
        System.out.println("💡 Windows API 비활성화 - Robot 클래스를 사용하세요");
        return false; // Always return false to use Robot fallback
    }
    
    /**
     * 현재 마우스 위치 (시뮬레이션)
     */
    public Point getCurrentMousePosition() {
        System.out.println("📍 마우스 위치 요청 (시뮬레이션)");
        return new Point(0, 0); // Default position
    }
    
    /**
     * 좌클릭 (시뮬레이션)
     */
    public CompletableFuture<Void> leftClickDirect() {
        System.out.println("🖱️ 좌클릭 요청 (시뮬레이션)");
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 우클릭 (시뮬레이션)
     */
    public CompletableFuture<Void> rightClickDirect() {
        System.out.println("🖱️ 우클릭 요청 (시뮬레이션)");
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 스크롤 (시뮬레이션)
     */
    public CompletableFuture<Void> scrollDirect(String direction, int amount) {
        System.out.println("📜 스크롤 요청 (시뮬레이션): " + direction + ", 양: " + amount);
        return CompletableFuture.completedFuture(null);
    }
}