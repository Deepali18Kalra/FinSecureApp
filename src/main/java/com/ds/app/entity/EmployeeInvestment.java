package com.ds.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.ds.app.enums.ComplianceStatus;
import com.ds.app.enums.InvestmentType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class EmployeeInvestment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType investmentType;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", nullable = true)
    private CompanyMutualFund mutualFund;

    @Column(length = 150)
    private String securityName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal declaredAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceStatus complianceStatus = ComplianceStatus.PENDING_REVIEW;

    
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(length = 500)
    private String reviewNote;

    @CreationTimestamp
    @Column(name = "declared_at", nullable = false, updatable = false)
    private LocalDateTime declaredAt;
}
