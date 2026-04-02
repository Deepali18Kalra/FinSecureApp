package com.ds.app.service.Impl;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.RegularizationRequestdto;
import com.ds.app.dto.RegularizationResponse;
import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.entity.RegularizationRequest;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.enums.RegularizationRequestStatus;
import com.ds.app.exception.DuplicateRegularizationException;
import com.ds.app.exception.InvalidDateRangeException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.RegularizationRequestMapper;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.repository.IRegularizationRequestRepository;
import com.ds.app.service.IEmailService;
import com.ds.app.service.IRegularizationRequestService;
import com.ds.app.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public RegularizationResponse applyRegularization(RegularizationRequestdto request) {
        Employee me = securityUtils.getLoggedInEmployee();

        if (request.getDate().isAfter(LocalDate.now())) {
            throw new InvalidDateRangeException("Regularization cannot be applied for a future date");
        }

        boolean alreadyPending = regularizationRepository.existsByEmployeeUserIdAndDateAndStatus(
                me.getUserId(), request.getDate(), RegularizationRequestStatus.PENDING);

        if (alreadyPending) {
            throw new DuplicateRegularizationException("Pending regularization already exists for date: " + request.getDate());
        }

        RegularizationRequest rr = RegularizationRequest.builder()
                .employee(me)
                .date(request.getDate())
                .reason(request.getReason())
                .punchInTime(request.getPunchInTime())
                .punchOutTime(request.getPunchOutTime())
                .status(RegularizationRequestStatus.PENDING)
                .build();

        RegularizationRequest saved = regularizationRepository.save(rr);

        // notify manager for new regularization request
        emailService.notifyManagerForNewRegularization(me, saved);

        return regularizationMapper.mapToResponse(saved);
    }

    @Override
    public List<RegularizationResponse> getMyRegularizationRequests(RegularizationRequestStatus status) {
        Employee me = securityUtils.getLoggedInEmployee();

        List<RegularizationRequest> list;
        if (status == null) {
            list = regularizationRepository.findByEmployeeUserIdOrderByDateDesc(me.getUserId());
        } else {
            list = regularizationRepository.findByEmployeeUserIdAndStatusOrderByDateDesc(me.getUserId(), status);
        }

        return list.stream().map(regularizationMapper::mapToResponse).toList();
    }

    @Override
    public List<RegularizationResponse> getPendingRegularizationsForHr() {
        Employee hr = securityUtils.getLoggedInEmployee();

        return regularizationRepository
                .findByEmployee_Manager_UserIdAndStatusOrderByDateDesc(hr.getUserId(), RegularizationRequestStatus.PENDING)
                .stream()
                .map(regularizationMapper::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RegularizationResponse reviewRegularization(Long requestId, ApprovalRequest request) {
        Employee hr = securityUtils.getLoggedInEmployee();

        RegularizationRequest rr = regularizationRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Regularization request not found with id: " + requestId));

        if (rr.getEmployee().getManager() == null || !rr.getEmployee().getManager().getUserId().equals(hr.getUserId())) {
            throw new ResourceNotFoundException("Regularization request not found with id: " + requestId);
        }

        if (rr.getStatus() != RegularizationRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING request can be reviewed.");
        }

        if (request.getStatus() == ApprovalStatus.APPROVED) {
            rr.setStatus(RegularizationRequestStatus.APPROVED);

            Attendance attendance = attendanceRepo.findByEmployeeUserIdAndDate(rr.getEmployee().getUserId(), rr.getDate())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Attendance not found for employeeId: " + rr.getEmployee().getUserId() + " and date: " + rr.getDate()
                    ));

            if (rr.getPunchInTime() != null) {
                attendance.setPunchInTime(rr.getPunchInTime());
            }

            if (rr.getPunchOutTime() != null) {
                attendance.setPunchOutTime(rr.getPunchOutTime());
            }

            attendance.setIsRegularized(true);
            attendance.setStatus(AttendanceStatus.MANUAL_PUNCH);

        } else {
            rr.setStatus(RegularizationRequestStatus.REJECTED);
            rr.setRejectionReason(request.getRejectionReason());
        }

        rr.setApprovedBy(hr);
        rr.setApprovalDate(LocalDate.now());

        RegularizationRequest saved = regularizationRepository.save(rr);

        // notify employee for final decision
        emailService.notifyEmployeeForRegularizationDecision(saved.getEmployee(), saved);

        return regularizationMapper.mapToResponse(saved);
    }
}