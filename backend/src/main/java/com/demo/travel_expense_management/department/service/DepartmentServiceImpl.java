package com.demo.travel_expense_management.department.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.department.dto.request.CreateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentSummaryResponse;
import com.demo.travel_expense_management.department.entity.Department;
import com.demo.travel_expense_management.department.mapper.DepartmentMapper;
import com.demo.travel_expense_management.department.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code())) {
            throw new BusinessException("Department code already exists: " + request.code());
        }

        Department department = departmentMapper.toEntity(request);
        department.setActive(request.active());

        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        departmentMapper.updateEntity(request, department);

        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Override
    public DepartmentResponse findById(Long id) {
        return departmentRepository.findById(id)
            .map(departmentMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Override
    public Page<DepartmentSummaryResponse> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable)
            .map(departmentMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setActive(true);
        departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setActive(false);
        departmentRepository.save(department);
    }
}
