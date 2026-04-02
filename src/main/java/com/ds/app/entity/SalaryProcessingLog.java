package com.ds.app.entity;

import com.ds.app.enums.SalaryProcessingStatus;
import com.ds.app.enums.SalarySkipReason;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Data
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Table(name = "salary_processing_log")
public class SalaryProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Employee.class)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_job_id", nullable = false)
    private SalaryJob salaryJob;

    @Column(nullable = false)
    private YearMonth salaryMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private SalarySkipReason skipReason;

    @Column(length = 500)
    private String details;  // human readable explanation

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SalaryProcessingStatus processingStatus; // SKIPPED / FAILED / SUCCESS 

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime loggedAt;
}
