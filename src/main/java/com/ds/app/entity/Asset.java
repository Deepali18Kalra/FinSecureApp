package com.ds.app.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.enums.AssetStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

    
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;
 
    @Column(unique = true,nullable = false)
    private String assetTag;//LAP-101
    
    private String category;
    
    private String name;
    
    private Integer totalUnits;
    private Integer allocatedUnits;
    private Integer availableUnits;
    
    
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
    
    private Long createdBy;//employeeId of HR/Admin
  
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetAllocation> allocations;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetIssue> issues;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetEscalation> escalations;
    
    @PrePersist
    public void prePersist()
    {

    	if (allocatedUnits == null) {
            allocatedUnits = 0;
        }
        if (availableUnits == null && totalUnits != null) {
            availableUnits = totalUnits;
        }

    	createdAt=LocalDateTime.now();
    	status=AssetStatus.AVAILABLE;
    }
      
	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public String getAssetTag() {
		return assetTag;
	}

	public void setAssetTag(String assetTag) {
		this.assetTag = assetTag;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AssetStatus getStatus() {
		return status;
	}

	public void setStatus(AssetStatus status) {
		this.status = status;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<AssetAllocation> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<AssetAllocation> allocations) {
		this.allocations = allocations;
	}

	public List<AssetIssue> getIssues() {
		return issues;
	}

	public void setIssues(List<AssetIssue> issues) {
		this.issues = issues;
	}

	public List<AssetEscalation> getEscalations() {
		return escalations;
	}

	public void setEscalations(List<AssetEscalation> escalations) {
		this.escalations = escalations;
	}

	public Integer getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}

	public Integer getAllocatedUnits() {
		return allocatedUnits;
	}

	public void setAllocatedUnits(Integer allocatedUnits) {
		this.allocatedUnits = allocatedUnits;
	}

	public Integer getAvailableUnits() {
		return availableUnits;
	}

	public void setAvailableUnits(Integer availableUnits) {
		this.availableUnits = availableUnits;
	}
	
	
	

}