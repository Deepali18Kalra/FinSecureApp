package com.ds.app.service;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.RegularizationRequestDTO;
import com.ds.app.dto.response.RegularizationResponse;
import com.ds.app.enums.RegularizationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRegularizationRequestService {

    RegularizationResponse applyForRegularization(RegularizationRequestDTO request);

    Page<RegularizationResponse> getMyRegularizationRequests(
            RegularizationRequestStatus status,
            Integer month,
            Integer year,
            Pageable pageable
    );

    List<RegularizationResponse> getPendingRegularizationsForManager();

    RegularizationResponse processRegularization(Long requestId, ApprovalRequest request);
}