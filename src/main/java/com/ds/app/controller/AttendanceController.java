package com.ds.app.controller;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.service.IAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	
	private final IAttendanceService attendanceService;
	
	// Employee Endpoints
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
	@PostMapping("/punch-in")
	public ResponseEntity<AttendanceResponse> punchIn() {
		AttendanceResponse response = attendanceService.punchIn();
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
	@PostMapping("/punch-out")
	public ResponseEntity<AttendanceResponse> punchOut() {
		AttendanceResponse response = attendanceService.punchOut();
		return ResponseEntity.ok(response);
	}

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
	@GetMapping()
	public ResponseEntity<List<AttendanceResponse>> getMyAttendance(
			@RequestParam Integer month,
			@RequestParam Integer year
			) {
		List<AttendanceResponse> response = attendanceService.getMyAttendance(month, year);
		return ResponseEntity.ok(response);
	}

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
	@GetMapping("/date/{date}")
	public ResponseEntity<AttendanceResponse> getMyAttendanceByDate(
			@PathVariable LocalDate date) {
		AttendanceResponse response = attendanceService.getMyAttendanceByDate(date);
		return ResponseEntity.ok(response);
	}
	
	// HR Endpoints
    @PreAuthorize("hasAuthority('HR')")
	@GetMapping("/employee/{employeeId}")
	public ResponseEntity<Page<AttendanceResponse>> getEmployeeAttendance(
			@PathVariable Long employeeId,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Integer year,
			@PageableDefault(size = 10, page = 0, sort = "date", direction = Sort.Direction.DESC)
			Pageable pageable
			) {
		Page<AttendanceResponse> pageResponse = attendanceService.getEmployeeAttendance(employeeId, month, year, pageable);
		return ResponseEntity.ok(pageResponse);
	}

    @PreAuthorize("hasAuthority('HR')")
	@GetMapping("/all")
	public ResponseEntity<Page<AttendanceResponse>> getAllAttendanceByDate(
			@RequestParam LocalDate date,
			@PageableDefault(size = 10, page = 0, sort = "attendanceId", direction = Sort.Direction.DESC)
			Pageable pageable
			) {
		Page<AttendanceResponse> pageResponse = attendanceService.getAllAttendanceByDate(date, pageable);
		return ResponseEntity.ok(pageResponse);
	}
}

