package com.ds.app.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ds.app.enums.RegularizationRequestStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class RegularizationRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	
	private LocalDate date;
	private String reason;
	private LocalTime punchInTime;
	private LocalTime punchOutTime;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private RegularizationRequestStatus status = RegularizationRequestStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approved_by")
	private Employee approvedBy;
	
	private LocalDate approvalDate;
}
