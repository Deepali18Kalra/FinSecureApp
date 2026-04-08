package com.ds.app.dto;

import jakarta.validation.constraints.NotNull;

public class RaiseEscalationRequestDTO {

	@NotNull(message="Asset ID is required")
    private Long assetId;
	@NotNull(message="Employee ID is required")
    private Long employeeId;
	@NotNull(message="Escalation reason is required")
	private String reason;

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

//    public Integer getRaisedBy() { return raisedBy; }
//    public void setRaisedBy(Integer raisedBy) { this.raisedBy = raisedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}