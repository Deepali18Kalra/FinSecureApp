package com.ds.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import com.ds.app.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class SalaryRecord {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_job_id", nullable = false)
    private SalaryJob salaryJob;

    
    @Column(name = "salary_month", nullable = false)
    private YearMonth salaryMonth;

   
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal grossSalary;

    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal deductions;

    // grossSalary - deductions
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private EmployeeBankAccount bankAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // set only when paymentStatus = CREDITED
    private LocalDateTime creditedAt;
}
