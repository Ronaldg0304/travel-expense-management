package com.demo.travel_expense_management.costcenter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.costcenter.dto.request.CreateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.request.UpdateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterResponse;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterSummaryResponse;
import com.demo.travel_expense_management.costcenter.entity.CostCenter;
import com.demo.travel_expense_management.costcenter.mapper.CostCenterMapper;
import com.demo.travel_expense_management.costcenter.repository.CostCenterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CostCenterServiceImpl implements CostCenterService {

    private final CostCenterRepository costCenterRepository;
    private final CostCenterMapper costCenterMapper;

    @Override
    @Transactional
    public CostCenterResponse create(CreateCostCenterRequest request) {
        if (costCenterRepository.existsByCode(request.code())) {
            throw new BusinessException("Cost center code already exists: " + request.code());
        }

        CostCenter costCenter = costCenterMapper.toEntity(request);
        costCenter.setActive(request.active());

        return costCenterMapper.toResponse(costCenterRepository.save(costCenter));
    }

    @Override
    @Transactional
    public CostCenterResponse update(Long id, UpdateCostCenterRequest request) {
        CostCenter costCenter = costCenterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cost center not found with id: " + id));

        costCenterMapper.updateEntity(request, costCenter);

        return costCenterMapper.toResponse(costCenterRepository.save(costCenter));
    }

    @Override
    public CostCenterResponse findById(Long id) {
        return costCenterRepository.findById(id)
            .map(costCenterMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Cost center not found with id: " + id));
    }

    @Override
    public Page<CostCenterSummaryResponse> findAll(Pageable pageable) {
        return costCenterRepository.findAll(pageable)
            .map(costCenterMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        CostCenter costCenter = costCenterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cost center not found with id: " + id));
        costCenter.setActive(true);
        costCenterRepository.save(costCenter);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        CostCenter costCenter = costCenterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cost center not found with id: " + id));
        costCenter.setActive(false);
        costCenterRepository.save(costCenter);
    }
}
