package com.ds.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long balanceId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	private Integer year;
	
	@Builder.Default
	private Integer sickLeaveBalance = 10;
	
	@Builder.Default
	private Integer casualLeaveBalance = 8;
	
	@Builder.Default
	private Integer earnedLeaveBalance = 12;
}
