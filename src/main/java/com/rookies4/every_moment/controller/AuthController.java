package com.rookies4.every_moment.controller;


import com.rookies4.every_moment.controller.dto.AuthDTO.*;
import com.rookies4.every_moment.service.AuthService;
import com.rookies4.every_moment.exception.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(BaseResponse.ok(service.register(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(service.login(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(service.refresh(req)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest req) {
        service.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }
}