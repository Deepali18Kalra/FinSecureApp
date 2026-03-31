package com.ds.app.mapper;

import org.springframework.stereotype.Component;

import com.ds.app.dto.RegularizationResponse;
import com.ds.app.entity.RegularizationRequest;

@Component
public class RegularizationRequestMapper {

    public RegularizationResponse mapToResponse(RegularizationRequest r) {
        String employeeName = r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName();
        String approvedByName = r.getApprovedBy() == null
                ? null
                : r.getApprovedBy().getFirstName() + " " + r.getApprovedBy().getLastName();

        return RegularizationResponse.builder()
                .requestId(r.getRequestId())
                .employeeId(r.getEmployee().getUserId())
                .employeeName(employeeName)
                .date(r.getDate())
                .punchInTime(r.getPunchInTime())
                .punchOutTime(r.getPunchOutTime())
                .reason(r.getReason())
                .status(r.getStatus())
                .approvedByName(approvedByName)
                .build();
    }
}