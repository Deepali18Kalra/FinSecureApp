package com.ds.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ds.app.dto.EmployeeAssetHoldingReportDTO;
import com.ds.app.dto.EmployeeAssetHoldingReportDTO.HeldAssetDetail;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AllocationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeReportService;

@Service
public class EmployeeReportServiceImpl implements EmployeeReportService {

    private final EmployeeRepository employeeRepository;
    private final AssetAllocationRepository allocationRepository;

    public EmployeeReportServiceImpl(
            EmployeeRepository employeeRepository,
            AssetAllocationRepository allocationRepository) {

        this.employeeRepository = employeeRepository;
        this.allocationRepository = allocationRepository;
    }

    @Override
    public EmployeeAssetHoldingReportDTO getAssetsHeldByEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id: " + employeeId));

        List<AssetAllocation> activeAllocations =
                allocationRepository.findByEmployeeAndStatus(
                        employee, AllocationStatus.ACTIVE);

        List<HeldAssetDetail> heldAssets =
                activeAllocations.stream()
                        .map(allocation -> {
                            HeldAssetDetail detail = new HeldAssetDetail();
                            detail.setAssetId(
                                    allocation.getAsset().getAssetId());
                            detail.setAssetName(
                                    allocation.getAsset().getName());
                            detail.setCategory(
                                    allocation.getAsset().getCategory());
                            detail.setAllocatedDate(
                                    allocation.getAllocatedDate());
                            return detail;
                        })
                        .collect(Collectors.toList());

        EmployeeAssetHoldingReportDTO report = new EmployeeAssetHoldingReportDTO();
        report.setEmployeeId(employee.getUserId());
        report.setAssetsHeld(heldAssets);

        return report;
    }
}