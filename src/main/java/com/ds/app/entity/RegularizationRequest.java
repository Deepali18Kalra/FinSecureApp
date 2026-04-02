package com.ds.app.entity;

import com.ds.app.enums.RegularizationRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private String rejectionReason;
}
