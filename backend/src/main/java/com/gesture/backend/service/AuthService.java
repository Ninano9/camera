package com.gesture.backend.service;

import com.gesture.backend.dto.UserDto;
import com.gesture.backend.entity.User;
import com.gesture.backend.repository.UserRepository;
import com.gesture.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Transactional
    public UserDto.Response register(UserDto.CreateRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        return userService.convertToDto(savedUser);
    }

    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        try {
            // 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // 사용자 정보 조회
            User user = userRepository.findActiveUserByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            // JWT 토큰 생성
            String accessToken = jwtUtil.generateToken(userDetails, user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getId());

            log.info("User logged in successfully: {}", user.getEmail());

            return UserDto.LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userService.convertToDto(user))
                    .build();

        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw new RuntimeException("Invalid credentials");
        }
    }

    public UserDto.LoginResponse refreshToken(String refreshToken) {
        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String userEmail = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findActiveUserByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            String newAccessToken = jwtUtil.generateToken(userDetails, user.getId());
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, user.getId());

            return UserDto.LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .user(userService.convertToDto(user))
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
