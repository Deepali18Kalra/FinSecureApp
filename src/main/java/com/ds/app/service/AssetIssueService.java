package com.ds.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.AssetIssueDTO;
import com.ds.app.dto.RaiseIssueRequestDTO;

public interface AssetIssueService {

	AssetIssueDTO raiseIssue(RaiseIssueRequestDTO request,Long loggedInUserId);
	AssetIssueDTO updateIssueStatus(Long issueId,String status,Long hrOrAdminUserId);

	Page<AssetIssueDTO> getAllIssuesPaginated(Pageable pageable);

}
