package com.ds.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ds.app.enums.AllocationStatus;
import com.ds.app.enums.AssetStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asset_allocation")
public class AssetAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;

    private Long allocatedBy;//FK EmployeeId of HR/Admin

    private LocalDate allocatedDate;

    private LocalDate deallocatedDate;

    @Enumerated(EnumType.STRING)
    private AllocationStatus status;

    @PrePersist
    public void prePersist()
    {
    	allocatedDate=LocalDate.now();
    	status=AllocationStatus.ACTIVE;
    }
    
	public Long getAllocationId() {
		return allocationId;
	}

	public void setAllocationId(Long allocationId) {
		this.allocationId = allocationId;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Long getAllocatedBy() {
		return allocatedBy;
	}

	public void setAllocatedBy(Long hrOrAdminUserId) {
		this.allocatedBy = hrOrAdminUserId;
	}

	public LocalDate getAllocatedDate() {
		return allocatedDate;
	}

	public void setAllocatedDate(LocalDate allocatedDate) {
		this.allocatedDate = allocatedDate;
	}

	public LocalDate getDeallocatedDate() {
		return deallocatedDate;
	}

	public void setDeallocatedDate(LocalDate deallocatedDate) {
		this.deallocatedDate = deallocatedDate;
	}

	public AllocationStatus getStatus() {
		return status;
	}

	public void setStatus(AllocationStatus status) {
		this.status = status;
	}

}