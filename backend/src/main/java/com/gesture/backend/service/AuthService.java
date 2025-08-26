package com.gesture.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    public String getAuthStatus() {
        log.info("Auth service status requested");
        return "Authentication service is running";
    }
}