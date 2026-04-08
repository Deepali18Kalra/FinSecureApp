package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "employees")
//@PrimaryKeyJoinColumn(name="user_Id")

public class Employee extends AppUser {

	//boolean isAssetEscalated=false; 
	@Column(name="has_active_asset_escalation",nullable=false)
	private Boolean hasActiveAssetEscalation=false;
	
	@OneToMany(mappedBy = "employee")
	private List<AssetAllocation> assetAllocations;
    
	@OneToMany(mappedBy = "employee")
    private List<AssetIssue> assetIssues;
    
	
	@OneToMany(mappedBy = "employee")
    private List<AssetEscalation> assetEscalations;
	
    public Employee() {
		
	}

	public Boolean isHasActiveAssetEscalation() {
		return hasActiveAssetEscalation;
	}

	public void setHasActiveAssetEscalation(Boolean hasActiveAssetEscalation) {
		this.hasActiveAssetEscalation = hasActiveAssetEscalation;
	}
    
    
    
    
}
