package com.ds.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.enums.TimesheetStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
		uniqueConstraints = @UniqueConstraint(
				columnNames = {"employee_id", "month", "year"}
				)
		)
public class Timesheet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timesheetId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	
	private Integer month;
	private Integer year;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private TimesheetStatus status = TimesheetStatus.DRAFT;
	
	private LocalDateTime submittedAt;
	
	@Builder.Default
	private Double totalMonthlyHours = 0.0;
	
	@OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<TimesheetEntry> timesheetEntries;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approvedBy")
	private Employee approvedBy;
	
	private LocalDate approvalDate;
	private String rejectionReason;
	
}
