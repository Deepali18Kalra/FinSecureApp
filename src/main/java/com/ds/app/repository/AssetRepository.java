package com.ds.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Long>{

	Optional<Asset> findByAssetTag(String assetTag);

	List<Asset> findByNameContainingIgnoreCaseOrAssetTagContainingIgnoreCaseOrCategoryContainingIgnoreCase(
			String name, String assetTag, String category
			);
	
}
