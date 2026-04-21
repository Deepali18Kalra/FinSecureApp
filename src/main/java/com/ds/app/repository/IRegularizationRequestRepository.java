package com.ds.app.repository;

import com.ds.app.entity.RegularizationRequest;
import com.ds.app.enums.RegularizationRequestStatus;
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
public interface IRegularizationRequestRepository extends JpaRepository<RegularizationRequest, Long> {

    Optional<RegularizationRequest> findByRequestIdAndEmployeeUserId(Long requestId, Long employeeId);

    List<RegularizationRequest> findByEmployeeUserIdOrderByDateDesc(Long employeeId);

    List<RegularizationRequest> findByEmployeeUserIdAndStatusOrderByDateDesc(Long employeeId, RegularizationRequestStatus status);

    List<RegularizationRequest> findByEmployee_Manager_UserIdAndStatusOrderByDateDesc(Long managerId, RegularizationRequestStatus status);

    boolean existsByEmployeeUserIdAndDateAndStatus(Long employeeId, LocalDate date, RegularizationRequestStatus status);

    @Query("""
           SELECT r
           FROM RegularizationRequest r
           WHERE r.employee.userId = :employeeId
             AND (:status IS NULL OR r.status = :status)
             AND (:month IS NULL OR FUNCTION('MONTH', r.date) = :month)
             AND (:year IS NULL OR FUNCTION('YEAR', r.date) = :year)
           ORDER BY r.date DESC
           """)
    Page<RegularizationRequest> searchMyRegularizations(
            @Param("employeeId") Long employeeId,
            @Param("status") RegularizationRequestStatus status,
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable
    );
}