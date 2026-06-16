package com.demo.travel_expense_management.department.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.department.dto.request.CreateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverSummaryResponse;
import com.demo.travel_expense_management.department.entity.Department;
import com.demo.travel_expense_management.department.entity.DepartmentApprover;
import com.demo.travel_expense_management.department.mapper.DepartmentApproverMapper;
import com.demo.travel_expense_management.department.repository.DepartmentApproverRepository;
import com.demo.travel_expense_management.department.repository.DepartmentRepository;
import com.demo.travel_expense_management.user.entity.User;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentApproverServiceImpl implements DepartmentApproverService {

    private final DepartmentApproverRepository departmentApproverRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentApproverMapper departmentApproverMapper;

    @Override
    @Transactional
    public DepartmentApproverResponse create(CreateDepartmentApproverRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.departmentId()));

        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        if (!user.isActive()) {
            throw new BusinessException("User is not active");
        }

        if (departmentApproverRepository.existsByDepartmentIdAndApproverId(request.departmentId(), request.userId())) {
            throw new BusinessException("Approver already assigned to this department");
        }

        DepartmentApprover departmentApprover = departmentApproverMapper.toEntity(request);
        departmentApprover.setDepartment(department);
        departmentApprover.setApprover(user);
        departmentApprover.setActive(request.active());

        return departmentApproverMapper.toResponse(departmentApproverRepository.save(departmentApprover));
    }

    @Override
    @Transactional
    public DepartmentApproverResponse update(Long id, UpdateDepartmentApproverRequest request) {
        DepartmentApprover departmentApprover = departmentApproverRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department approver not found with id: " + id));

        departmentApproverMapper.updateEntity(request, departmentApprover);

        return departmentApproverMapper.toResponse(departmentApproverRepository.save(departmentApprover));
    }

    @Override
    public DepartmentApproverResponse findById(Long id) {
        return departmentApproverRepository.findById(id)
            .map(departmentApproverMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Department approver not found with id: " + id));
    }

    @Override
    public Page<DepartmentApproverSummaryResponse> findAll(Pageable pageable) {
        return departmentApproverRepository.findAll(pageable)
            .map(departmentApproverMapper::toSummaryResponse);
    }

    @Override
    public List<DepartmentApproverSummaryResponse> findByDepartment(Long departmentId) {
        return departmentApproverMapper.toSummaryResponseList(
            departmentApproverRepository.findByDepartmentId(departmentId));
    }

    @Override
    @Transactional
    public void activate(Long id) {
        DepartmentApprover departmentApprover = departmentApproverRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department approver not found with id: " + id));
        departmentApprover.setActive(true);
        departmentApproverRepository.save(departmentApprover);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        DepartmentApprover departmentApprover = departmentApproverRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department approver not found with id: " + id));
        departmentApprover.setActive(false);
        departmentApproverRepository.save(departmentApprover);
    }
}
