package com.ds.app.entity;

import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.entity.SalaryJob;
import com.ds.app.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Data
public class SalaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY , targetEntity = Employee.class)    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Many-to-One with SalaryJob
    // many records belong to one job run
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_job_id", nullable = false)
    private SalaryJob salaryJob;

    // stored as YYYY-MM string via YearMonthConverter
    @Column(name = "salary_month", nullable = false)
    private YearMonth salaryMonth;

    // read from UserProfile.currentSalary at job execution time
    @Column(nullable = false)
    private Double grossSalary;

    // insurance premium + unpaid leave + TDS
    @Column(nullable = false)
    private Double deductions;

    // grossSalary - deductions
    @Column(nullable = false)
    private Double netSalary;

    // Many-to-One with EmployeeBankAccount
    // same bank account receives salary every month
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private EmployeeBankAccount bankAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // set only when paymentStatus → CREDITED
    private LocalDateTime creditedAt;
}