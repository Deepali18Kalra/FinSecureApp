package com.ds.app.service.impl;

import com.ds.app.dto.AssetAvailabilityReportDTO;
import com.ds.app.dto.AssetAvailabilityReportDTO.AllocationDetail;
import com.ds.app.dto.EscalationReportDTO;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AllocationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.repository.AssetRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetReportServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetAllocationRepository allocationRepository;

    @Mock
    private AssetEscalationRepository escalationRepository;

    @InjectMocks
    private AssetReportServiceImpl assetReportService;

    // ============================================================
    // Helpers
    // ============================================================

    private Asset buildAsset(Long id) {
        Asset asset = new Asset();
        asset.setAssetId(id);
        asset.setName("Laptop");
        asset.setCategory("HARDWARE");
        asset.setTotalUnits(10);
        asset.setAllocatedUnits(4);
        asset.setAvailableUnits(6);
        return asset;
    }

    private Employee buildEmployee(Long id) {
        Employee emp = new Employee();
        emp.setUserId(id);
        emp.setUsername("emp" + id);
        return emp;
    }

    private AssetAllocation buildAllocation(Employee emp, LocalDate date) {
        AssetAllocation allocation = new AssetAllocation();
        allocation.setEmployee(emp);
        allocation.setAllocatedDate(date);
        allocation.setStatus(AllocationStatus.ACTIVE);
        return allocation;
    }

    @Test
    void getAssetAvailabilityReport_ShouldReturnCorrectAllocationDetails() {

        Asset asset = buildAsset(1L);
        Employee emp1 = buildEmployee(101L);
        Employee emp2 = buildEmployee(102L);

        AssetAllocation alloc1 =
                buildAllocation(emp1, LocalDate.now().minusDays(2));
        AssetAllocation alloc2 =
                buildAllocation(emp2, LocalDate.now().minusDays(1));

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        when(allocationRepository.findByAssetAndStatus(
                asset, AllocationStatus.ACTIVE))
                .thenReturn(List.of(alloc1, alloc2));

        AssetAvailabilityReportDTO report =
                assetReportService.getAssetAvailabilityReport(1L);

        assertNotNull(report);
        assertEquals(1L, report.getAssetId());
        assertEquals("Laptop", report.getAssetName());
        assertEquals(10, report.getTotalUnits());
        assertEquals(4, report.getAllocatedUnits());
        assertEquals(6, report.getAvailableUnits());
        assertEquals(2, report.getAllocatedTo().size());

        AllocationDetail detail = report.getAllocatedTo().get(0);
        assertNotNull(detail.getEmployeeId());
        assertNotNull(detail.getAllocatedDate());
    }


    @Test
    void getAssetAvailabilityReport_ShouldThrowException_WhenAssetNotFound() {

        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> assetReportService.getAssetAvailabilityReport(99L)
        );

        assertTrue(ex.getMessage().contains("Asset not found"));
    }

}