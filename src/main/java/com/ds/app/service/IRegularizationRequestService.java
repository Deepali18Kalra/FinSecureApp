package com.ds.app.service;

import java.util.List;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.RegularizationRequestdto;
import com.ds.app.dto.RegularizationResponse;

public interface IRegularizationRequestService {

    RegularizationResponse applyRegularization(RegularizationRequestdto request);

    List<RegularizationResponse> getMyRegularizationRequests(String status);

    List<RegularizationResponse> getPendingRegularizationsForHr();

    RegularizationResponse reviewRegularization(Long requestId, ApprovalRequest request);
}