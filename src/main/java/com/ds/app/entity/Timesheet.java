package com.ds.app.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timesheet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timesheetId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	private Integer month;
	private Integer year;
	
	@Builder.Default
	private TimesheetStatus status = TimesheetStatus.DRAFT;
	
	private LocalDateTime submittedAt;
	
	@OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<TimesheetEntry> timesheetEntries;
	
}
