package com.ds.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ds.app.enums.EscalationStatus;

public class EscalatedAssetReportDTO {

	
	private Long assetId;
	private String assetName;
	private String category;
	
	private Long employeeId;
	private LocalDate allocatedDate;

	private boolean hasActiveEscalation;
	private String escalationReason;
	private String escalationStatus;
	
	public EscalatedAssetReportDTO() {
		super();
	}
	public EscalatedAssetReportDTO(long l, String string, LocalDateTime now, String string2, EscalationStatus open,
			long m, String string3) {
		// TODO Auto-generated constructor stub
	}
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
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public LocalDate getAllocatedDate() {
		return allocatedDate;
	}
	public void setAllocatedDate(LocalDate allocatedDate) {
		this.allocatedDate = allocatedDate;
	}
	public boolean isHasActiveEscalation() {
		return hasActiveEscalation;
	}
	public void setHasActiveEscalation(boolean hasActiveEscalation) {
		this.hasActiveEscalation = hasActiveEscalation;
	}
	public String getEscalationReason() {
		return escalationReason;
	}
	public void setEscalationReason(String escalationReason) {
		this.escalationReason = escalationReason;
	}
	public String getEscalationStatus() {
		return escalationStatus;
	}
	public void setEscalationStatus(String escalationStatus) {
		this.escalationStatus = escalationStatus;
	}
	
}
