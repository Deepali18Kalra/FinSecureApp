package com.ds.app.repository;

import com.ds.app.dto.response.LeaveStatusResponse;
import com.ds.app.entity.Leave;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ILeaveRepository extends JpaRepository<Leave, Long> {
    Page<Leave> findByEmployee_Manager_UserId(Long managerId, Pageable pageable);

    @Query("""
    select new com.ds.app.dto.response.LeaveStatusResponse(
        l.leaveId,
        l.startDate,
        l.endDate,
        l.totalDays,
        l.leaveType,
        l.status,
        l.approvalDate,
        l.rejectionReason
    )
    from Leave l
    where l.employee.userId = :employeeId
    and (:status is null or l.status in :status)
    and (:year is null or year(l.startDate) = :year)
    and (:month is null or month(l.startDate) = :month)
    """)
    Page<LeaveStatusResponse> searchLeaveByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("status") LeaveStatus status,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    Page<Leave> findByEmployee_Manager_UserIdAndStatusIn(Long managerId, List<LeaveStatus> status, Pageable pageable);
    
    Optional<Leave> findByLeaveIdAndEmployeeUserId(Long leaveId, Long employeeId);

    @Query("""
        SELECT l
        FROM Leave l
        WHERE l.employee.userId = :userId
          AND l.status = :status
          AND l.leaveType <> :unpaidType
          AND l.startDate <= :toDate
          AND l.endDate >= :fromDate
    """)
    List<Leave> findApprovedPaidLeavesOverlappingRange(
            @Param("userId") Long userId,
            @Param("status") LeaveStatus status,
            @Param("unpaidType") LeaveType unpaidType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    List<Leave> findByStatusAndStartDateBefore(LeaveStatus status, LocalDate todayDate);
}