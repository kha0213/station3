package com.yl.station3.service;

import com.yl.station3.domain.user.User;
import com.yl.station3.dto.auth.JwtResponse;
import com.yl.station3.dto.auth.LoginRequest;
import com.yl.station3.dto.auth.SignUpRequest;
import com.yl.station3.exception.BusinessException;
import com.yl.station3.exception.ErrorCode;
import com.yl.station3.repository.UserRepository;
import com.yl.station3.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        userRepository.save(user);
        log.info("새 사용자 가입: {}", request.getEmail());
    }

    public JwtResponse login(LoginRequest request) {
        try {
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // JWT 토큰 생성
            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);

            log.info("사용자 로그인: {}", request.getEmail());

            return new JwtResponse(token, user.getEmail(), user.getName());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
