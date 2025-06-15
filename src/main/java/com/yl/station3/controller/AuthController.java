package com.yl.station3.controller;

import com.yl.station3.dto.auth.JwtResponse;
import com.yl.station3.dto.auth.LoginRequest;
import com.yl.station3.dto.auth.SignUpRequest;
import com.yl.station3.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
