package com.demo.travel_expense_management.security.service;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.security.dto.request.LoginRequest;
import com.demo.travel_expense_management.security.dto.response.LoginResponse;
import com.demo.travel_expense_management.security.dto.response.LoginResponse.UserInfo;
import com.demo.travel_expense_management.security.jwt.JwtService;
import com.demo.travel_expense_management.user.entity.User;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!user.isActive()) {
            throw new BusinessException("User is inactive");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String accessToken = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(accessToken);
        long expiresIn = (expiration.getTime() - System.currentTimeMillis()) / 1000;

        UserInfo userInfo = new UserInfo(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole()
        );

        return new LoginResponse(accessToken, null, expiresIn, userInfo);
    }
}
