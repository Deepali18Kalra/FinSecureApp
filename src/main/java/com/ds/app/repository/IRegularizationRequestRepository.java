package com.ds.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.RegularizationRequest;
import com.ds.app.enums.RegularizationRequestStatus;

@Repository
public interface IRegularizationRequestRepository extends JpaRepository<RegularizationRequest, Long> {

    Optional<RegularizationRequest> findByRequestIdAndEmployeeUserId(Long requestId, Long employeeId);

    List<RegularizationRequest> findByEmployeeUserIdOrderByDateDesc(Long employeeId);

    List<RegularizationRequest> findByEmployeeUserIdAndStatusOrderByDateDesc(Long employeeId, RegularizationRequestStatus status);

    List<RegularizationRequest> findByEmployeeHrUserIdAndStatusOrderByDateDesc(Long hrId, RegularizationRequestStatus status);

    boolean existsByEmployeeUserIdAndDateAndStatus(Long employeeId, LocalDate date, RegularizationRequestStatus status);
}