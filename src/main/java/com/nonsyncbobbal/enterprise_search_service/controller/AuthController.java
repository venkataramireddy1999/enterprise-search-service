package com.nonsyncbobbal.enterprise_search_service.controller;

import com.nonsyncbobbal.enterprise_search_service.dto.AuthRequest;
import com.nonsyncbobbal.enterprise_search_service.dto.AuthResponse;
import com.nonsyncbobbal.enterprise_search_service.dto.RegisterRequest;
import com.nonsyncbobbal.enterprise_search_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request
    ) {

        String token =
                authService.login(request);

        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }
}
