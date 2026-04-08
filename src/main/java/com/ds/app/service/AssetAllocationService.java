package com.ds.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.AllocateAssetRequestDTO;
import com.ds.app.dto.AssetAllocationDTO;

public interface AssetAllocationService {

	AssetAllocationDTO allocateAsset(AllocateAssetRequestDTO request, Long hrOrAdminUserId);
	AssetAllocationDTO deallocateAsset(Long allocationId,Long hrOrAdminUserId);
	
	Page<AssetAllocationDTO> getAllAllocationsPaginated(Pageable pageable);
	
}
