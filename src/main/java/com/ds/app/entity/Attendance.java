package com.ds.app.entity;

import com.ds.app.enums.AttendanceStatus;
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

	private Integer totalMinutesWorked;

    @Builder.Default
    private Boolean isLate = false;

	@Builder.Default
	private Boolean isRegularized = false;
}
