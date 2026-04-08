package com.ds.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ds.app.dto.AssetDTO;
import com.ds.app.entity.Asset;
import com.ds.app.enums.AssetStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetRepository;
import com.ds.app.service.AssetService;


@Service
public class AssetServiceImpl implements AssetService {

	private final AssetRepository assetReposository;

	public AssetServiceImpl(AssetRepository assetReposository) {
		super();
		this.assetReposository = assetReposository;
	}
	
	@Override
	public AssetDTO createAsset(AssetDTO dto,Long creatorUserId) {
		Asset asset=new Asset();
		
		asset.setTotalUnits(dto.getTotalUnits());
		
		asset.setAssetTag(dto.getAssetTag());
		asset.setCategory(dto.getCategory());
		asset.setName(dto.getName());
		asset.setCreatedBy(creatorUserId);
		asset.setStatus(AssetStatus.AVAILABLE);
		
		Asset saved=assetReposository.save(asset);
		
		AssetDTO response=new AssetDTO();
		response.setAssetId(saved.getAssetId());
		response.setAssetTag(saved.getAssetTag());
		response.setCategory(saved.getCategory());
		response.setName(saved.getName());
		response.setStatus(saved.getStatus());
		response.setCreatedBy(saved.getCreatedBy());
		
		response.setTotalUnits(asset.getTotalUnits());
		response.setAllocatedUnits(asset.getAllocatedUnits());
		response.setAvailableUnits(asset.getAvailableUnits());
		
		
		return response;
		
	}

	@Override
	public AssetDTO updateAsset(Long assetId, AssetDTO dto,Long updaterUserId) {
		Asset asset=assetReposository.findById(assetId).orElseThrow(()->new ResourceNotFoundException("Asset Not Found"));
		asset.setCategory(dto.getCategory());
		asset.setName(dto.getName());
		assetReposository.save(asset);
		
		AssetDTO response=new AssetDTO();
		response.setAssetId(asset.getAssetId());
		response.setAssetTag(asset.getAssetTag());
		response.setCategory(asset.getCategory());
		response.setName(asset.getName());
		response.setStatus(asset.getStatus());
		response.setCreatedBy(asset.getCreatedBy());
		return response;
				
	}

	@Override
	public AssetDTO getAssetById(Long assetId) {
		Asset asset=assetReposository.findById(assetId).orElseThrow(()->new ResourceNotFoundException("Asset Not Found"));
		AssetDTO dto=new AssetDTO();
		dto.setAssetId(asset.getAssetId());
		dto.setAssetTag(asset.getAssetTag());
		dto.setCategory(asset.getCategory());
		dto.setName(asset.getName());
		dto.setStatus(asset.getStatus());
		dto.setCreatedBy(asset.getCreatedBy());
		dto.setAllocatedUnits(asset.getAllocatedUnits());
		dto.setAvailableUnits(asset.getAvailableUnits());
		dto.setTotalUnits(asset.getTotalUnits());
		return dto;
	}

	@Override
	public List<AssetDTO> getAllAssets() {
		
		return assetReposository.findAll()
				.stream().map(asset->{
						AssetDTO dto=new AssetDTO();
						dto.setAssetId(asset.getAssetId());
						dto.setAssetTag(asset.getAssetTag());
						dto.setCategory(asset.getCategory());
						dto.setName(asset.getName());
						dto.setStatus(asset.getStatus());
						dto.setCreatedBy(asset.getCreatedBy());
						dto.setAllocatedUnits(asset.getAllocatedUnits());
						dto.setAvailableUnits(asset.getAvailableUnits());
						dto.setTotalUnits(asset.getTotalUnits());
						return dto;
				}).collect(Collectors.toList());
	
	}

	@Override
	public void deleteAsset(Long assetId, Long deleterUserId) {

		if(!assetReposository.existsById(assetId))
		{
			throw new ResourceNotFoundException("Asset Not Found");
		}
		
		assetReposository.deleteById(assetId);
		
	}
	
	@Override
	public Page<AssetDTO> getAllAssetsPaginated(Pageable pageable)
	{	
		return assetReposository.findAll(pageable)
				.map(asset->{
					AssetDTO dto=new AssetDTO();
					dto.setAssetId(asset.getAssetId());
					dto.setAssetTag(asset.getAssetTag());
					dto.setCategory(asset.getCategory());
					dto.setName(asset.getName());
					dto.setAssetId(asset.getAssetId());
					dto.setAllocatedUnits(asset.getAllocatedUnits());
					dto.setAvailableUnits(asset.getAvailableUnits());
					dto.setTotalUnits(asset.getTotalUnits());
					dto.setStatus(asset.getStatus());
					dto.setCreatedBy(asset.getCreatedBy());
					
					return dto;
					
				});
		
		
	}

	
}
