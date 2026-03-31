package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.LeaveBalance;

@Repository
public interface ILeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    boolean existsByEmployeeUserIdAndYear(Long employeeId, Integer year);

    Optional<LeaveBalance> findByEmployeeUserIdAndYear(Long employeeId, Integer year);
}