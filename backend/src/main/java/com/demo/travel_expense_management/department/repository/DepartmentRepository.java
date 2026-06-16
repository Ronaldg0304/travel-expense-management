package com.demo.travel_expense_management.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
