package com.ds.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.AssetDTO;

public interface AssetService {

	AssetDTO createAsset(AssetDTO dto,Long creatorUserId);
	AssetDTO updateAsset(Long assetId, AssetDTO dto,Long updaterUserId);
	AssetDTO getAssetById(Long assetId);
	List<AssetDTO> getAllAssets();
	void deleteAsset(Long assetId, Long deleterUserId);
	
	Page<AssetDTO> getAllAssetsPaginated(Pageable pageable);
	
}
