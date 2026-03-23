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
public class TimesheetEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timesheetEntryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "timesheet_id")
	private Timesheet timesheet;
	
	private LocalDate date;
	private String taskDescription;
	private Double hoursWorked;

}
