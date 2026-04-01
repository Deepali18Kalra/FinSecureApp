package com.ds.app.entity;

import com.ds.app.enums.ComplianceStatus;
import com.ds.app.enums.InvestmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Builder
@Data
public class EmployeeInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empInvestmentId;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType investmentType;

    // Many-to-One with MutualFundMaster
    // many employees can invest in the same fund
    // nullable — only set when investmentType = MUTUAL_FUND
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", nullable = true)
    private FinanceInvestment mutualFund;

    
    // only for DIRECT_EQUITY or BONDS
    
    @Column(length = 150)
    private String securityName;

    
    private Double declaredAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceStatus complianceStatus = ComplianceStatus.PENDING_REVIEW;

    // Finance user who reviewed this investment
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(length = 500)
    private String reviewNote;

    @CreationTimestamp
    @Column(name = "declared_at", nullable = false, updatable = false)
    private LocalDateTime declaredAt;
    
    @UpdateTimestamp
    @Column(name= "updated_at")
    private LocalDateTime upadtedAt;
    
}