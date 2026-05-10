package com.nonsyncbobbal.enterprise_search_service.service;

import com.nonsyncbobbal.enterprise_search_service.dto.AuthRequest;
import com.nonsyncbobbal.enterprise_search_service.dto.RegisterRequest;
import com.nonsyncbobbal.enterprise_search_service.entity.Role;
import com.nonsyncbobbal.enterprise_search_service.entity.User;
import com.nonsyncbobbal.enterprise_search_service.repository.UserRepository;
import com.nonsyncbobbal.enterprise_search_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    public String login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        return jwtService.generateToken(
                request.getUsername()
        );
    }
}
