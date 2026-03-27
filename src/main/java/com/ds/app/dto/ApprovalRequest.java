package com.ds.app.dto;

import com.ds.app.enums.ApprovalStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotBlank(message = "Status is required")
    private ApprovalStatus status;

    private String rejectionReason;
}