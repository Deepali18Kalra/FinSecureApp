package com.ds.app.entity;

import com.ds.app.enums.FundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class FinanceInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mutualFundId;

    @Column(nullable = false, unique = true, length = 150)
    private String fundName;

    @Column(nullable = false, unique = true, length = 30)
    private String fundCode;

    @Column(nullable = false, length = 80)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundStatus status;

    // Finance user who added this fund to whitelist/blacklist
    @Column(nullable = false)
    private Long addedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // One fund → many employee investments in that fund
    @OneToMany(mappedBy = "mutualFund", fetch = FetchType.LAZY)
    private List<EmployeeInvestment> employeeInvestments;
}