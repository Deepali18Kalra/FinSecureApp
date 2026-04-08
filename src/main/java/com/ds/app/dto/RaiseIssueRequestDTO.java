package com.ds.app.dto;

import jakarta.validation.constraints.NotNull;

public class RaiseIssueRequestDTO {

	@NotNull(message="Asset ID is required")
    private Long assetId;
	@NotNull(message="Issue type is required")
	private String issueType;
	@NotNull(message="Issue description is required")
    private String description;

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

//    public Integer getEmployeeId() { return employeeId; }
//    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}