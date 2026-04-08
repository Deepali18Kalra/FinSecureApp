package com.ds.app.dto;

import java.time.LocalDateTime;
import com.ds.app.enums.EscalationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetEscalationDTO {

    private Long escalationId;
    @NotNull
    private Long assetId;
    @NotNull
    private Long employeeId;
    
    private Long raisedBy;
    private String raisedByName;
    
    @NotBlank
    private String reason;
    
    private EscalationStatus status;
    private LocalDateTime createdAt;

    public Long getEscalationId() { return escalationId; }
    public void setEscalationId(Long escalationId) { this.escalationId = escalationId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getRaisedBy() { return raisedBy; }
    public void setRaisedBy(Long raisedBy) { this.raisedBy = raisedBy; }

    public String getRaisedByName() {
		return raisedByName;
	}
	public void setRaisedByName(String raisedByName) {
		this.raisedByName = raisedByName;
	}
	public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public EscalationStatus getStatus() { return status; }
    public void setStatus(EscalationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}