package com.gesture.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileService {

    public String getProfileStatus() {
        log.info("Profile service status requested");
        return "Profile service is running";
    }
}