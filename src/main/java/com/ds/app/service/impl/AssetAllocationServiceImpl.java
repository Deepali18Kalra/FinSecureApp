package com.ds.app.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ds.app.dto.AllocateAssetRequestDTO;
import com.ds.app.dto.AssetAllocationDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AllocationStatus;
import com.ds.app.enums.AssetStatus;
import com.ds.app.exception.BadRequestException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetAllocationService;

@Service
public class AssetAllocationServiceImpl implements AssetAllocationService{
	
	private final AssetRepository assetRepository;
	private final AssetAllocationRepository assetAllocationRepository;
	private final iAppUserRepository appUserRepository;
	

	public AssetAllocationServiceImpl(AssetRepository assetRepository,
			AssetAllocationRepository assetAllocationRepository, iAppUserRepository appUserRepository) {
		super();
		this.assetRepository = assetRepository;
		this.assetAllocationRepository = assetAllocationRepository;
		this.appUserRepository = appUserRepository;
	}

	@Override
	public AssetAllocationDTO allocateAsset(AllocateAssetRequestDTO request,Long hrOrAdminUserId) {
		Asset asset=assetRepository.findById(request.getAssetId()).orElseThrow(()->new ResourceNotFoundException("Asset not found"));

		if(asset.getAvailableUnits()<=0)
		{
			throw new BadRequestException("Asset not available");
		}
		
		

		AppUser user = appUserRepository.findById(request.getEmployeeId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!(user instanceof Employee employee)) {
			throw new ResourceNotFoundException("User is not an Employee");
		}

		
		AssetAllocation allocation=new AssetAllocation();
		allocation.setAsset(asset);
		allocation.setEmployee(employee);
		allocation.setAllocatedBy(hrOrAdminUserId);
		allocation.setAllocatedDate(LocalDate.now());
		allocation.setStatus(AllocationStatus.ACTIVE);
	//new
		if(asset.getAvailableUnits()<=0)
		{
			throw new BadRequestException("No units available to allocate.");
		}
		
		asset.setAllocatedUnits(asset.getAllocatedUnits()+1);
		asset.setAvailableUnits(asset.getAvailableUnits()-1);
		assetAllocationRepository.save(allocation);
		//new
		asset.setStatus(AssetStatus.ALLOCATED);
		assetRepository.save(asset);
		
		AssetAllocationDTO dto=new AssetAllocationDTO();
		dto.setAllocationId(allocation.getAllocationId());
		dto.setAssetId(asset.getAssetId());
//		dto.setEmployeeId(allocation.getEmployeeId());
		dto.setEmployeeId(employee.getUserId());
		dto.setAllocatedBy(allocation.getAllocatedBy());
		dto.setAllocatedDate(allocation.getAllocatedDate());
		dto.setStatus(allocation.getStatus());

		dto.setAllocatedByName(appUserRepository.findById(allocation.getAllocatedBy())
        .map(c->c.getUsername())
        .orElse(null)
				);
	
		return dto;
	}

	@Override
	public AssetAllocationDTO deallocateAsset(Long allocationId,Long hrOrAdminUserId) {
		AssetAllocation allocation=assetAllocationRepository.findById(allocationId)
				.orElseThrow(()-> new ResourceNotFoundException("Allocation not found"));
		
		allocation.setDeallocatedDate(LocalDate.now());
		allocation.setStatus(AllocationStatus.RETURNED);
		
		assetAllocationRepository.save(allocation);
		
		Asset asset=allocation.getAsset();
		asset.setStatus(AssetStatus.AVAILABLE);
		
		asset.setAllocatedUnits(asset.getAllocatedUnits()-1);
		asset.setAvailableUnits(asset.getAvailableUnits()+1);
		assetRepository.save(asset);
		

		AssetAllocationDTO dto = new AssetAllocationDTO();
        dto.setAllocationId(allocation.getAllocationId());
        dto.setAssetId(asset.getAssetId());
        dto.setEmployeeId(allocation.getEmployee().getUserId());
        dto.setAllocatedBy(allocation.getAllocatedBy());
        dto.setAllocatedDate(allocation.getAllocatedDate());
        dto.setDeallocatedDate(allocation.getDeallocatedDate());
        dto.setStatus(allocation.getStatus());

        dto.setAllocatedByName(
        		appUserRepository.findById(allocation.getAllocatedBy())
        		.map(user -> user.getUsername())
        		.orElse(null));

        
        
        return dto;

		
	}

	@Override
	public Page<AssetAllocationDTO> getAllAllocationsPaginated(Pageable pageable) {

		return assetAllocationRepository.findAll(pageable)
				.map(allocation->{
					AssetAllocationDTO dto=new AssetAllocationDTO();
					dto.setAllocationId(allocation.getAllocationId());
					dto.setAssetId(allocation.getAsset().getAssetId());
					dto.setEmployeeId(allocation.getEmployee().getUserId());
					dto.setAllocatedBy(allocation.getAllocatedBy());
					dto.setAllocatedDate(allocation.getAllocatedDate());
					dto.setDeallocatedDate(allocation.getDeallocatedDate());
					dto.setStatus(allocation.getStatus());
					dto.setAllocatedByName(
						    appUserRepository.findById(allocation.getAllocatedBy())
						        .map(user -> user.getUsername())
						        .orElse(null)
						);
					
					
					return dto;
					
				});
	}

}
