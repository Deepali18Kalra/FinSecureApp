package com.ds.app.service.impl;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.RegularizationRequestDTO;
import com.ds.app.dto.response.RegularizationResponse;
import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.entity.RegularizationRequest;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.enums.RegularizationRequestStatus;
import com.ds.app.exception.DuplicateRegularizationException;
import com.ds.app.exception.ForbiddenException;
import com.ds.app.exception.InvalidDateRangeException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.RegularizationRequestMapper;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.repository.IRegularizationRequestRepository;
import com.ds.app.service.IEmailService;
import com.ds.app.service.IRegularizationRequestService;
import com.ds.app.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegularizationRequestServiceImpl implements IRegularizationRequestService {

    private final IRegularizationRequestRepository regularizationRepository;
    private final IAttendanceRepository attendanceRepo;
    private final RegularizationRequestMapper regularizationMapper;
    private final SecurityUtils securityUtils;
    private final IEmailService emailService;

    @Override
    @Transactional
    public RegularizationResponse applyForRegularization(RegularizationRequestDTO request) {
        Employee loggedInEmp = securityUtils.getLoggedInEmployee();

        if (request.getDate().isAfter(LocalDate.now())) {
            throw new InvalidDateRangeException("Regularization cannot be applied for a future date");
        }

        boolean alreadyPending = regularizationRepository.existsByEmployeeUserIdAndDateAndStatus(
                loggedInEmp.getUserId(), request.getDate(), RegularizationRequestStatus.PENDING);

        if (alreadyPending) {
            throw new DuplicateRegularizationException("Pending regularization already exists for date: " + request.getDate());
        }

        if (request.getPunchInTime() == null && request.getPunchOutTime() == null) {
            throw new IllegalArgumentException("You must provide either a punch-in or punch-out time to regularize.");
        }

        if (request.getDate().isEqual(LocalDate.now())) {
            LocalTime now = LocalTime.now();
            if (request.getPunchInTime() != null && request.getPunchInTime().isAfter(now)) {
                throw new IllegalArgumentException("Punch-in time cannot be in the future.");
            }
            if (request.getPunchOutTime() != null && request.getPunchOutTime().isAfter(now)) {
                throw new IllegalArgumentException("Punch-out time cannot be in the future.");
            }
        }

        RegularizationRequest rr = RegularizationRequest.builder()
                .employee(loggedInEmp)
                .date(request.getDate())
                .reason(request.getReason())
                .punchInTime(request.getPunchInTime())
                .punchOutTime(request.getPunchOutTime())
                .status(RegularizationRequestStatus.PENDING)
                .build();

        RegularizationRequest saved = regularizationRepository.save(rr);

        emailService.notifyManagerForNewRegularization(loggedInEmp, saved);

        return regularizationMapper.mapToResponse(saved);
    }

    @Override
    public Page<RegularizationResponse> getMyRegularizationRequests(
            RegularizationRequestStatus status,
            Integer month,
            Integer year,
            Pageable pageable
    ) {
        Employee me = securityUtils.getLoggedInEmployee();

        return regularizationRepository
                .searchMyRegularizations(me.getUserId(), status, month, year, pageable)
                .map(regularizationMapper::mapToResponse);
    }

    @Override
    public List<RegularizationResponse> getPendingRegularizationsForManager() {
        Employee loggedInManager = securityUtils.getLoggedInEmployee();

        return regularizationRepository
                .findByEmployee_Manager_UserIdAndStatusOrderByDateDesc(
                        loggedInManager.getUserId(),
                        RegularizationRequestStatus.PENDING
                )
                .stream()
                .map(regularizationMapper::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RegularizationResponse processRegularization(Long requestId, ApprovalRequest request) {
        Employee loggedInManager = securityUtils.getLoggedInEmployee();

        RegularizationRequest regularizationReq = regularizationRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Regularization request not found with id: " + requestId));

        if (regularizationReq.getEmployee().getManager() == null
                || !regularizationReq.getEmployee().getManager().getUserId().equals(loggedInManager.getUserId())) {
            throw new ForbiddenException("You are not authorised to process this regularization request");
        }

        if (regularizationReq.getStatus() != RegularizationRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING request can be reviewed.");
        }

        if (request.getStatus().equals(ApprovalStatus.APPROVED)) {
            regularizationReq.setStatus(RegularizationRequestStatus.APPROVED);

            Attendance attendance = attendanceRepo.findByEmployeeUserIdAndDate(
                            regularizationReq.getEmployee().getUserId(),
                            regularizationReq.getDate()
                    )
                    .orElseGet(() -> {
                        Attendance newAttendance = Attendance.builder()
                                .employee(regularizationReq.getEmployee())
                                .date(regularizationReq.getDate())
                                .build();
                        return attendanceRepo.save(newAttendance);
                    });

            if (regularizationReq.getPunchInTime() != null) {
                attendance.setPunchInTime(regularizationReq.getPunchInTime());
                LocalTime threshold = LocalTime.of(12, 0);
                boolean isLateArrival = regularizationReq.getPunchInTime().isAfter(threshold);
                attendance.setIsLate(isLateArrival);
            }

            if (regularizationReq.getPunchOutTime() != null) {
                attendance.setPunchOutTime(regularizationReq.getPunchOutTime());
            }

            if (attendance.getPunchInTime() != null && attendance.getPunchOutTime() != null) {
                Duration duration = Duration.between(attendance.getPunchInTime(), attendance.getPunchOutTime());
                attendance.setTotalMinutesWorked((int) duration.toMinutes());
            }

            attendance.setIsRegularized(true);
            attendance.setStatus(AttendanceStatus.MANUAL_PUNCH);

        } else {
            regularizationReq.setStatus(RegularizationRequestStatus.REJECTED);
            regularizationReq.setRejectionReason(request.getRejectionReason());
        }

        regularizationReq.setApprovedBy(loggedInManager);
        regularizationReq.setApprovalDate(LocalDate.now());

        RegularizationRequest saved = regularizationRepository.save(regularizationReq);

        emailService.notifyEmployeeForRegularizationDecision(saved.getEmployee(), saved);

        return regularizationMapper.mapToResponse(saved);
    }
}