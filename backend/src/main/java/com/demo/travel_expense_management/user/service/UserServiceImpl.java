package com.demo.travel_expense_management.user.service;

import java.security.SecureRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.department.repository.DepartmentRepository;
import com.demo.travel_expense_management.user.dto.request.CreateUserRequest;
import com.demo.travel_expense_management.user.dto.request.UpdateUserRequest;
import com.demo.travel_expense_management.user.dto.response.UserResponse;
import com.demo.travel_expense_management.user.dto.response.UserSummaryResponse;
import com.demo.travel_expense_management.user.entity.User;
import com.demo.travel_expense_management.user.mapper.UserMapper;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists: " + request.email());
        }
        if (userRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new BusinessException("Document number already exists: " + request.documentNumber());
        }
        departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.departmentId()));

        User user = userMapper.toEntity(request);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(generateTemporaryPassword()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.departmentId()));

        userMapper.updateEntity(request, user);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public Page<UserSummaryResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }
}
