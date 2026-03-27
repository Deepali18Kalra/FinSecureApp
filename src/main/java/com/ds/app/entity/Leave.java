package com.ds.app.entity;

import java.time.LocalDate;

import com.ds.app.enums.LeaveStatus;
import com.ds.app.enums.LeaveType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee_leave")
public class Leave {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long leaveId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer totalDays;
	private String reason;
	
	@Enumerated(EnumType.STRING)
	private LeaveType leaveType;
	
	private LocalDate approvalDate;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LeaveStatus status = LeaveStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approved_by")
	private Employee approvedBy;
	
	private String rejectionReason;
}
