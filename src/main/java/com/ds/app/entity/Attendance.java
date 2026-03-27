package com.ds.app.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ds.app.enums.AttendanceStatus;

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
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attendanceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;

	private LocalDate date;
	private LocalTime punchInTime;
	private LocalTime punchOutTime;
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private AttendanceStatus status = AttendanceStatus.MISS_SWIPE;

	private Double hoursWorked;

	@Builder.Default
	private Boolean isRegularized = false;
}
