package com.ds.app.dto.request;

import com.ds.app.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "Status is required")
    private ApprovalStatus status;

    private String rejectionReason;
}