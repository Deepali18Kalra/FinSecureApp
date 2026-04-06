package com.ds.app.service.Impl;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.dto.TeamAttendanceReportRow;
import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.exception.ForbiddenException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.AttendanceMapper;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.repository.IEmployeeRepository;
import com.ds.app.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private SecurityUtils securityUtil;
    @Mock
    private IAttendanceRepository attendanceRepo;
    @Mock
    private IEmployeeRepository employeeRepo;
    @Mock
    private AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Employee employee;
    private Employee manager;

    @BeforeEach
    void setUp() {
        manager = Employee.builder().firstName("Manager").lastName("One").build();
        manager.setUserId(100L);
        employee = Employee.builder().firstName("Mayank").lastName("Singh").manager(manager).build();
        employee.setUserId(1L);
    }

    @Test
    void punchIn_shouldCreateNewAttendance_whenNoRecordForToday() {
        when(securityUtil.getLoggedInEmployee()).thenReturn(employee);
        when(attendanceRepo.findByEmployeeUserIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(Optional.empty());

        Attendance saved = Attendance.builder()
                .attendanceId(10L)
                .employee(employee)
                .date(LocalDate.now())
                .punchInTime(LocalTime.of(9, 0))
                .isLate(false)
                .build();

        when(attendanceRepo.save(any(Attendance.class))).thenReturn(saved);
        when(attendanceMapper.mapToResponse(saved)).thenReturn(new AttendanceResponse());

        AttendanceResponse response = attendanceService.punchIn();

        assertNotNull(response);
        verify(attendanceRepo).save(any(Attendance.class));
    }

    @Test
    void getMyAttendanceByDate_shouldThrow_whenNotFound() {
        when(securityUtil.getLoggedInEmployee()).thenReturn(employee);
        when(attendanceRepo.findByEmployeeUserIdAndDate(1L, LocalDate.of(2026, 3, 10)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> attendanceService.getMyAttendanceByDate(LocalDate.of(2026, 3, 10)));
    }

    @Test
    void getEmployeeAttendance_shouldThrowForbidden_whenNotReportingManager() {
        Employee otherManager = new Employee();
        otherManager.setUserId(999L);
        Employee loggedIn = new Employee();
        loggedIn.setUserId(500L);
        Employee target = new Employee();
        target.setUserId(2L);

        when(employeeRepo.findById(2L)).thenReturn(Optional.of(target));
        when(securityUtil.getLoggedInEmployee()).thenReturn(loggedIn);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());

        assertThrows(ForbiddenException.class,
                () -> attendanceService.getEmployeeAttendance(2L, 3, 2026, pageable));
    }

    @Test
    void getAllAttendanceByDate_shouldReturnMappedPage() {
        when(securityUtil.getLoggedInEmployee()).thenReturn(manager);

        Attendance attendance = Attendance.builder()
                .attendanceId(1L)
                .employee(employee)
                .date(LocalDate.of(2026, 3, 24))
                .status(AttendanceStatus.PRESENT)
                .build();

        Page<Attendance> attendancePage = new PageImpl<>(List.of(attendance));
        when(attendanceRepo.findByEmployee_Manager_UserIdAndDate(eq(100L), eq(LocalDate.of(2026, 3, 24)), any(Pageable.class)))
                .thenReturn(attendancePage);

        when(attendanceMapper.mapToResponse(attendance)).thenReturn(new AttendanceResponse());

        Page<AttendanceResponse> result = attendanceService.getAllAttendanceByDate(
                LocalDate.of(2026, 3, 24),
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getTeamAttendanceReport_shouldReturnRows() {
        when(securityUtil.getLoggedInEmployee()).thenReturn(manager);

        TeamAttendanceReportRow row = TeamAttendanceReportRow.builder()
                .employeeId(1L)
                .employeeName("Mayank Singh")
                .date(LocalDate.of(2026, 3, 24))
                .status(AttendanceStatus.PRESENT)
                .build();

        when(attendanceRepo.findTeamAttendanceReportByManagerAndDate(100L, LocalDate.of(2026, 3, 24)))
                .thenReturn(List.of(row));

        List<TeamAttendanceReportRow> result =
                attendanceService.getTeamAttendanceReport(LocalDate.of(2026, 3, 24));

        assertEquals(1, result.size());
        assertEquals("Mayank Singh", result.get(0).getEmployeeName());
    }
}