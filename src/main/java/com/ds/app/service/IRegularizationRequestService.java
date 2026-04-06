package com.ds.app.service;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.RegularizationRequestDTO;
import com.ds.app.dto.RegularizationResponse;
import com.ds.app.enums.RegularizationRequestStatus;

import java.util.List;

public interface IRegularizationRequestService {

    RegularizationResponse applyForRegularization(RegularizationRequestDTO request);

    List<RegularizationResponse> getMyRegularizationRequests(RegularizationRequestStatus status);

    List<RegularizationResponse> getPendingRegularizationsForManager();

    RegularizationResponse processRegularization(Long requestId, ApprovalRequest request);
}