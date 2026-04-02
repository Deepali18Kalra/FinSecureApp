package com.ds.app.service;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.RegularizationRequestdto;
import com.ds.app.dto.RegularizationResponse;
import com.ds.app.enums.RegularizationRequestStatus;

import java.util.List;

public interface IRegularizationRequestService {

    RegularizationResponse applyRegularization(RegularizationRequestdto request);

    List<RegularizationResponse> getMyRegularizationRequests(RegularizationRequestStatus status);

    List<RegularizationResponse> getPendingRegularizationsForHr();

    RegularizationResponse reviewRegularization(Long requestId, ApprovalRequest request);
}