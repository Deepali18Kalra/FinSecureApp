package com.ds.app.dto;

public class AssetSearchResultDTO {

	private Long assetId;
	private String assetName;
	private String assetTag;
	private String category;
	
	private Integer totalUnits;
	private Integer allocatedUnits;
	private Integer availableUnits;
	
	private String status;

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
