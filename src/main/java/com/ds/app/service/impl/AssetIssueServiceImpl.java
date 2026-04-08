package com.ds.app.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ds.app.dto.AssetIssueDTO;
import com.ds.app.dto.RaiseIssueRequestDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetIssue;
import com.ds.app.entity.Employee;
import com.ds.app.enums.IssueStatus;
import com.ds.app.exception.BadRequestException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetIssueRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetIssueService;

@Service
public class AssetIssueServiceImpl implements AssetIssueService {
	
	private final iAppUserRepository appUserRepository;
	private final AssetRepository assetRepository;
	private final AssetIssueRepository issueRepository;
	
	
	
	public AssetIssueServiceImpl(iAppUserRepository appUserRepository, AssetRepository assetRepository,
			AssetIssueRepository issueRepository) {
		super();
		this.appUserRepository = appUserRepository;
		this.assetRepository = assetRepository;
		this.issueRepository = issueRepository;
	}
	
	@Override
	public AssetIssueDTO raiseIssue(RaiseIssueRequestDTO request,Long loggedInUserId) {
		Asset asset=assetRepository.findById(request.getAssetId()).orElseThrow(()->new ResourceNotFoundException("Asset not found"));

		AppUser appUser = appUserRepository.findById(loggedInUserId).orElseThrow(()->new ResourceNotFoundException("User not found"));

		if (!(appUser instanceof Employee employee)) {
			throw new BadRequestException("Only Employees/Hr/Admin can raise issues");
		}

		AssetIssue issue=new AssetIssue();
		issue.setAsset(asset);
		issue.setEmployee(employee);
		issue.setIssueType(request.getIssueType());
		issue.setDescription(request.getDescription());
		issue.setStatus(IssueStatus.OPEN);
		issue.setCreatedAt(LocalDateTime.now());
		issueRepository.save(issue);
		
		
		AssetIssueDTO dto=new AssetIssueDTO();
		
		dto.setIssueId(issue.getIssueId());
		dto.setAssetId(asset.getAssetId());
		//dto.setEmployeeId(issue.getEmployeeId());
		dto.setEmployeeId(employee.getUserId());
		dto.setIssueType(issue.getIssueType());
		dto.setDescription(issue.getDescription());
		dto.setStatus(issue.getStatus());
		dto.setCreatedAt(issue.getCreatedAt());
		
		return dto;
	}
	@Override
	public AssetIssueDTO updateIssueStatus(Long issueId, String status,Long hrOrAdminUserId) {
		
		AssetIssue issue=issueRepository.findById(issueId).orElseThrow(()-> new ResourceNotFoundException("Issue not found"));
		issue.setStatus(IssueStatus.valueOf(status));
		issue.setUpdatedAt(LocalDateTime.now());
		
		issueRepository.save(issue);
		
		AssetIssueDTO dto=new AssetIssueDTO();
		dto.setIssueId(issue.getIssueId());
		dto.setStatus(issue.getStatus());
		return dto;
	}

	@Override
	public Page<AssetIssueDTO> getAllIssuesPaginated(Pageable pageable) {

		return issueRepository.findAll(pageable)
            .map(issue -> {
                AssetIssueDTO dto = new AssetIssueDTO();
                dto.setIssueId(issue.getIssueId());
                dto.setAssetId(issue.getAsset().getAssetId());
                dto.setEmployeeId(issue.getEmployee().getUserId());
                dto.setIssueType(issue.getIssueType());
                dto.setDescription(issue.getDescription());
                dto.setStatus(issue.getStatus());
                dto.setCreatedAt(issue.getCreatedAt());
                return dto;
            });

	}

}
