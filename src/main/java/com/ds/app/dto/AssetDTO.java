package com.ds.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ds.app.enums.AssetStatus;



import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetDTO {

    private Long assetId;

    @NotNull(message="Asset tag cannot be null")
    @Size(min=2,max=30)
    private String assetTag;
    
    @NotNull(message="Category cannot be null")
    private String category;

    @NotNull(message="Name tag cannot be null")
    private String name;
    
    
    
    //new
    @NotNull(message="Total units must be provided")
    @Min(value=1,message="Total units must be atleast 1")
    private Integer totalUnits;
    private Integer allocatedUnits;
    private Integer availableUnits;
    
    
    //new
    
    
    //response
    private AssetStatus status;
    
    private Long createdBy;

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String assetTag) { this.assetTag = assetTag; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
	public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}