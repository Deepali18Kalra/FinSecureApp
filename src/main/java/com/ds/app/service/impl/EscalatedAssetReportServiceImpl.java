package com.ds.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ds.app.dto.EscalatedAssetReportDTO;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AllocationStatus;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.service.EscalatedAssetReportService;

@Service
public class EscalatedAssetReportServiceImpl implements EscalatedAssetReportService{

	
	private final AssetRepository assetRepository;
	private final AssetEscalationRepository escalationRepository;
	private final AssetAllocationRepository allocationRepository;
	
	public EscalatedAssetReportServiceImpl(AssetRepository assetRepository,
			AssetEscalationRepository escalationRepository, AssetAllocationRepository allocationRepository) {
		super();
		this.assetRepository = assetRepository;
		this.escalationRepository = escalationRepository;
		this.allocationRepository = allocationRepository;
	}

	@Override
	public List<EscalatedAssetReportDTO> getEscalationAwareAssetReport(Long assetId) {

		Asset asset=assetRepository.findById(assetId)
				.orElseThrow(()-> new ResourceNotFoundException("Asset not found with id: "+assetId));
		//fetch active
		List<AssetAllocation> activeAllocations=allocationRepository.findByAssetAndStatus(asset, AllocationStatus.ACTIVE);
		return activeAllocations.stream()
				.map(allocation-> {
					Employee employee=allocation.getEmployee();
					EscalatedAssetReportDTO report=new EscalatedAssetReportDTO();
					report.setAssetId(asset.getAssetId());
					report.setAssetName(asset.getName());
					report.setCategory(asset.getCategory());
					report.setEmployeeId(employee.getUserId());
					report.setAllocatedDate(allocation.getAllocatedDate());
					
					//checkfor escalation
					boolean hasActiveEscalation=Boolean.TRUE.equals(employee.isHasActiveAssetEscalation());
					report.setHasActiveEscalation(hasActiveEscalation);
					
					if(hasActiveEscalation) {
						escalationRepository.findFirstByEmployeeAndStatusOrderByCreatedAtDesc(employee,
								EscalationStatus.OPEN).ifPresent(escalation->{
									report.setEscalationReason(escalation.getReason());
									report.setEscalationStatus(escalation.getStatus().name());
								});
					}
					return report;
					
				}).collect(Collectors.toList());
		
	}

}
