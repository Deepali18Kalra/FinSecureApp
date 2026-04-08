package com.ds.app.service;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.RegularizationRequestDTO;
import com.ds.app.dto.response.RegularizationResponse;
import com.ds.app.enums.RegularizationRequestStatus;

import java.util.List;

public interface IRegularizationRequestService {

    RegularizationResponse applyForRegularization(RegularizationRequestDTO request);

    List<RegularizationResponse> getMyRegularizationRequests(RegularizationRequestStatus status);

    List<RegularizationResponse> getPendingRegularizationsForManager();

    RegularizationResponse processRegularization(Long requestId, ApprovalRequest request);
}