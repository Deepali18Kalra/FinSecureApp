package com.ds.app.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leave {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long leaveId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer totalDays;
	private String reason;
	
	private LeaveType leaveType;
	
	@Builder.Default
	private LeaveStatus status = LeaveStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approved_by")
	private Employee approvedBy;
}
