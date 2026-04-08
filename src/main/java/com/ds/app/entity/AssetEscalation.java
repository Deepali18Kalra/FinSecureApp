package com.ds.app.entity;

import java.time.LocalDateTime;

import com.ds.app.enums.EscalationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "asset_escalations")
public class AssetEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long escalationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;//FK-Employee(employee who misused the asset)
    

    @ManyToOne
    @JoinColumn(name = "raised_by")
    private AppUser raisedBy;

//    private Integer raisedBy;//FK-HR/Admin who raised escalation
    private String reason;

    @Enumerated(EnumType.STRING)
    private EscalationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist()
    {
    	createdAt=LocalDateTime.now();
    	status=EscalationStatus.OPEN;
    }
    
    @PreUpdate
    public void preUpdate()
    {
    	updatedAt=LocalDateTime.now();
    }

    
	public Long getEscalationId() {
		return escalationId;
	}

	public void setEscalationId(Long escalationId) {
		this.escalationId = escalationId;
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

	public AppUser getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(AppUser raisedBy) {
		this.raisedBy = raisedBy;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public EscalationStatus getStatus() {
		return status;
	}

	public void setStatus(EscalationStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}