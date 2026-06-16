package com.demo.travel_expense_management.department.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.department.entity.DepartmentApprover;

public interface DepartmentApproverRepository extends JpaRepository<DepartmentApprover, Long> {

    List<DepartmentApprover> findByDepartmentId(Long departmentId);

    boolean existsByDepartmentIdAndApproverId(Long departmentId, Long userId);

    Optional<DepartmentApprover> findByDepartmentIdAndApproverId(Long departmentId, Long userId);
}
