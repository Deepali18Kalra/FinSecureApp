package com.ds.app.dto;

import java.time.LocalDate;
import com.ds.app.enums.AllocationStatus;

public class AssetAllocationDTO {

    private Long allocationId;
    private Long assetId;
    private Long employeeId;
    
    
    private Long allocatedBy;
    private String allocatedByName;
    
    private LocalDate allocatedDate;
    private LocalDate deallocatedDate;
    private AllocationStatus status;

    public Long getAllocationId() { return allocationId; }
    public void setAllocationId(Long allocationId) { this.allocationId = allocationId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getAllocatedBy() { return allocatedBy; }
    public void setAllocatedBy(Long allocatedBy) { this.allocatedBy = allocatedBy; }

    public String getAllocatedByName() {
		return allocatedByName;
	}
	public void setAllocatedByName(String allocatedByName) {
		this.allocatedByName = allocatedByName;
	}
	public LocalDate getAllocatedDate() { return allocatedDate; }
    public void setAllocatedDate(LocalDate allocatedDate) { this.allocatedDate = allocatedDate; }

    public LocalDate getDeallocatedDate() { return deallocatedDate; }
    public void setDeallocatedDate(LocalDate deallocatedDate) { this.deallocatedDate = deallocatedDate; }

    public AllocationStatus getStatus() { return status; }
    public void setStatus(AllocationStatus status) { this.status = status; }
}