package com.ds.app.dto;

public class AssetUtilizationReportDTO {

	private Long assetId;
	private String assetName;
	private String category;
	
	private Integer totalUnits;
	private Integer allocatedUnits;
	private Integer avaialableUnits;
	
	private Double utilizationPercentage;

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

	public Integer getAvaialableUnits() {
		return avaialableUnits;
	}

	public void setAvaialableUnits(Integer avaialableUnits) {
		this.avaialableUnits = avaialableUnits;
	}

	public Double getUtilizationPercentage() {
		return utilizationPercentage;
	}

	public void setUtilizationPercentage(Double utilizationPercentage) {
		this.utilizationPercentage = utilizationPercentage;
	}
	
}
