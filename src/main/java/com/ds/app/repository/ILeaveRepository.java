package com.ds.app.repository;

import com.ds.app.dto.LeaveStatusResponse;
import com.ds.app.entity.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ILeaveRepository extends JpaRepository<Leave, Long> {
    Page<Leave> findByEmployeeHrUserId(Long hrId, Pageable pageable);

    @Query("""
            select new com.ds.app.dto.LeaveStatusResponse(
                            l.leaveId,
                            l.startDate,
                            l.endDate,
                            l.leaveType,
                            l.status,
                            l.approvalDate,
                            l.rejectionReason
                        )
            from Leave l
            where l.leaveId =:leaveId
            """)
    LeaveStatusResponse findByLeaveId(@Param("leaveId") Long leaveId);
}