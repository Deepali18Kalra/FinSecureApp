package com.ds.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.AssetEscalationDTO;
import com.ds.app.dto.RaiseEscalationRequestDTO;

public interface AssetEscalationService {

	AssetEscalationDTO raiseEscalation(RaiseEscalationRequestDTO request,Long hrOrAdminUserId);
	AssetEscalationDTO updateEscalationStatus(Long escalationId,String status,Long hrOrAdminUserId);
	Page<AssetEscalationDTO> getAllEscalationsPaginated(Pageable pageable);
	List<AssetEscalationDTO> getAllEscalations();
	
}
