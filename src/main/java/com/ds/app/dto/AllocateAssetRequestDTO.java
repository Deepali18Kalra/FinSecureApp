package com.ds.app.dto;

import jakarta.validation.constraints.NotNull;

public class AllocateAssetRequestDTO {

	@NotNull(message = "Asset id is required")
    private Long assetId;
	@NotNull(message = "Employee id is required")
    private Long employeeId;
//	@NotNull
//    private Integer allocatedBy;
//
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
//
//    public Integer getAllocatedBy() { return allocatedBy; }
//    public void setAllocatedBy(Integer allocatedBy) { this.allocatedBy = allocatedBy; }
}