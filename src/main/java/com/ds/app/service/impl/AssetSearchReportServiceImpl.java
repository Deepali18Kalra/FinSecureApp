package com.ds.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ds.app.dto.AssetAvailabilityReportDTO;
import com.ds.app.dto.AssetSearchResultDTO;
import com.ds.app.entity.Asset;
import com.ds.app.repository.AssetRepository;
import com.ds.app.service.AssetReportService;
import com.ds.app.service.AssetSearchReportService;

@Service
public class AssetSearchReportServiceImpl implements AssetSearchReportService {

	private final AssetRepository assetRepository;
	
	public AssetSearchReportServiceImpl(AssetRepository assetRepository) {
		super();
		this.assetRepository = assetRepository;
	}

	@Override
	public List<AssetSearchResultDTO> searchAssets(String keyword) {

		if(!StringUtils.hasText(keyword))
		{
			return List.of();
		}
		
		List<Asset> assets=assetRepository.findByNameContainingIgnoreCaseOrAssetTagContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword, keyword);
		return assets.stream().map(asset->{
			AssetSearchResultDTO dto=new AssetSearchResultDTO();
			dto.setAssetId(asset.getAssetId());
			dto.setAssetName(asset.getName());
			dto.setAssetTag(asset.getAssetTag());
			dto.setCategory(asset.getCategory());
			dto.setTotalUnits(asset.getTotalUnits());
			dto.setAllocatedUnits(asset.getAllocatedUnits());
			dto.setAvailableUnits(asset.getAvailableUnits());
			dto.setStatus(asset.getStatus().name());
			return dto;
			
		}).collect(Collectors.toList());
				
		
	}


}
