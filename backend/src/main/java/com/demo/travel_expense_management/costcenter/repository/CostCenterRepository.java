package com.demo.travel_expense_management.costcenter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.demo.travel_expense_management.costcenter.entity.CostCenter;

public interface CostCenterRepository extends JpaRepository<CostCenter, Long>, JpaSpecificationExecutor<CostCenter> {

    Optional<CostCenter> findByCode(String code);

    boolean existsByCode(String code);
}
