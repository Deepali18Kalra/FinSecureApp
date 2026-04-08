package com.ds.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetAllocation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AllocationStatus;

@Repository
public interface AssetAllocationRepository extends JpaRepository<AssetAllocation, Long> {
	
	List<AssetAllocation> findByEmployee_UserId(Integer userId);	List<AssetAllocation> findByStatus(AllocationStatus status);
	List<AssetAllocation> findByAsset_AssetId(Long assetId);
	List<AssetAllocation> findByAssetAndStatus(Asset asset, AllocationStatus status);
	List<AssetAllocation> findByEmployeeAndStatus(Employee employee,AllocationStatus status);

}
