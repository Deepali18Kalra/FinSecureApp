package com.ds.app.repository;

import com.ds.app.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ILeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    boolean existsByEmployeeUserIdAndYear(Long employeeId, Integer year);

    Optional<LeaveBalance> findByEmployeeUserIdAndYear(Long employeeId, Integer year);
}