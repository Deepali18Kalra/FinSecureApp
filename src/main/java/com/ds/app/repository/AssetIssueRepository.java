package com.ds.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.AssetIssue;
import com.ds.app.enums.IssueStatus;

@Repository
public interface AssetIssueRepository extends JpaRepository<AssetIssue, Long>{

	List<AssetIssue> findByEmployee_UserId(Integer userId);	List<AssetIssue> findByStatus(IssueStatus status);
	List<AssetIssue> findByAsset_AssetId(Long assetId);
}
