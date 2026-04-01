package com.ds.app.repository;

import com.ds.app.entity.SalaryRecord;
import com.ds.app.enums.PaymentStatus;

import java.time.YearMonth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

	Page<SalaryRecord> findByEmployee_UserId(Long employeeId, Pageable pageable);

	// prevent double salary for same employee same month
	boolean existsByEmployee_UserIdAndSalaryMonth(Long employeeId, YearMonth salaryMonth);

	Page<SalaryRecord> findByPaymentStatus(PaymentStatus status, Pageable pageable);
	
	Page<SalaryRecord> findBySalaryMonth(YearMonth salaryMonth, Pageable pageable);
}
