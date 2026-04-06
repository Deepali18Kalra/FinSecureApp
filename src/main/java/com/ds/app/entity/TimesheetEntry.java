package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
	@JoinColumn(name = "timesheet_id", nullable = false)
	private Timesheet timesheet;
	
	private LocalDate date;
	private String taskDescription;
	private Integer totalMinutesWorked;
	private Long projectId;
	private String projectName;
}
