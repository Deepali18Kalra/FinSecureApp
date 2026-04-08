package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ds.app.dto.EmployeeAssetHoldingReportDTO;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AllocationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeReportServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AssetAllocationRepository allocationRepository;

    @InjectMocks
    private EmployeeReportServiceImpl employeeReportService;

    @Test
    void getAssetsHeldByEmployee_shouldReturnHeldAssetsReport() {

        Employee employee = new Employee();
        employee.setUserId(1L);

        Asset asset = new Asset();
        asset.setAssetId(10L);
        asset.setName("Laptop");
        asset.setCategory("HARDWARE");

        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(employee);
        allocation.setAsset(asset);
        allocation.setStatus(AllocationStatus.ACTIVE);
        allocation.setAllocatedDate(LocalDate.now());

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));
        when(allocationRepository.findByEmployeeAndStatus(employee, AllocationStatus.ACTIVE))
                .thenReturn(List.of(allocation));

        EmployeeAssetHoldingReportDTO report =
                employeeReportService.getAssetsHeldByEmployee(1L);

        assertNotNull(report);
        assertEquals(1L, report.getEmployeeId());
        assertEquals(1, report.getAssetsHeld().size());

        EmployeeAssetHoldingReportDTO.HeldAssetDetail detail =
                report.getAssetsHeld().get(0);

        assertEquals(10L, detail.getAssetId());
        assertEquals("Laptop", detail.getAssetName());
        assertEquals("HARDWARE", detail.getCategory());
    }

    @Test
    void getAssetsHeldByEmployee_shouldThrowException_whenEmployeeNotFound() {

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                employeeReportService.getAssetsHeldByEmployee(99L)
        );
    }
}
