package com.ds.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ds.app.dto.AssetAvailabilityReportDTO;
import com.ds.app.dto.AssetAvailabilityReportDTO.AllocationDetail;
import com.ds.app.dto.EscalationReportDTO;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.enums.AllocationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.service.AssetReportService;

@Service
public class AssetReportServiceImpl implements AssetReportService {

    private final AssetRepository assetRepository;
    private final AssetAllocationRepository allocationRepository;
    
    private final AssetEscalationRepository escalationRepository;


    

    public AssetReportServiceImpl(AssetRepository assetRepository, AssetAllocationRepository allocationRepository,
			AssetEscalationRepository escalationRepository) {
		super();
		this.assetRepository = assetRepository;
		this.allocationRepository = allocationRepository;
		this.escalationRepository = escalationRepository;
	}

	@Override
    public AssetAvailabilityReportDTO getAssetAvailabilityReport(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Asset not found with id: " + assetId));

        List<AssetAllocation> activeAllocations =
                allocationRepository.findByAssetAndStatus(
                        asset, AllocationStatus.ACTIVE);

        List<AllocationDetail> allocationDetails =
                activeAllocations.stream()
                        .map(allocation -> {
                            AllocationDetail detail = new AllocationDetail();
                            detail.setEmployeeId(
                                    allocation.getEmployee().getUserId());
                            detail.setAllocatedDate(
                                    allocation.getAllocatedDate());
                            return detail;
                        })
                        .collect(Collectors.toList());

        AssetAvailabilityReportDTO report = new AssetAvailabilityReportDTO();
        report.setAssetId(asset.getAssetId());
        report.setAssetName(asset.getName());
        report.setCategory(asset.getCategory());

        report.setTotalUnits(asset.getTotalUnits());
        report.setAllocatedUnits(asset.getAllocatedUnits());
        report.setAvailableUnits(asset.getAvailableUnits());

        report.setAllocatedTo(allocationDetails);

        return report;
    }
	
	

@Override
    public List<EscalationReportDTO> getEscalationsOnDate(LocalDate date) {
        return escalationRepository.findEscalationsOnDate(date);
    }
    @Override
    public List<EscalationReportDTO> getEscalationsBetweenDates(
            LocalDateTime from,
            LocalDateTime to) {
        return escalationRepository.findEscalationsOnDate(from, to);
    }
    
    @Override
    public List<EscalationReportDTO> getEscalationsForEmployee(Long employeeId) {
        return escalationRepository.findEscalationsForEmployee(employeeId);
    }
}
