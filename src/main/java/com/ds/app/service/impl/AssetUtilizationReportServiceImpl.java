package com.ds.app.service.impl;

import org.springframework.stereotype.Service;

import com.ds.app.dto.AssetUtilizationReportDTO;
import com.ds.app.entity.Asset;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetRepository;
import com.ds.app.service.AssetUtilizationReportService;

@Service
public class AssetUtilizationReportServiceImpl implements AssetUtilizationReportService {

	private final AssetRepository assetRepository;
	
	public AssetUtilizationReportServiceImpl(AssetRepository assetRepository) {
		super();
		this.assetRepository = assetRepository;
	}


	@Override
	public AssetUtilizationReportDTO getAssetUtilization(Long assetId) {
		Asset asset= assetRepository.findById(assetId)
				.orElseThrow(()-> new ResourceNotFoundException("Asset not found with Id:"+ assetId));
		
		Integer totalUnits=asset.getTotalUnits();
		Integer allocatedUnits=asset.getAllocatedUnits();
		Integer availableUnits=asset.getAvailableUnits();
		
		double utilization=0.0;
		if(totalUnits!=null && totalUnits>0)
		{
			utilization=((double)allocatedUnits/totalUnits)*100;
		}
		
		AssetUtilizationReportDTO report=new AssetUtilizationReportDTO();
		report.setAssetId(asset.getAssetId());
		report.setAssetName(asset.getName());
		report.setCategory(asset.getCategory());
		
		
		report.setTotalUnits(totalUnits);
		report.setAllocatedUnits(allocatedUnits);
		report.setAvaialableUnits(availableUnits);
		report.setUtilizationPercentage(utilization);
		
		return report;
		
	}

}
