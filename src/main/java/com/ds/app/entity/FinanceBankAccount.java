package com.ds.app.entity;

import com.ds.app.enums.BankStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Data
public class FinanceBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;

    @Column(nullable = false, unique = true, length = 100)
    private String bankName;

    @Column(nullable = false, unique = true, length = 20)
    private String bankCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankStatus status;

    // Finance user who added this bank to whitelist/blacklist
    // plain Long — cross module, User is owned by Auth module
    @Column(nullable = false)
    private Long addedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // One bank institution → many employee accounts
    // e.g. ICICI has many employees banking with it
    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<EmployeeBankAccount> employeeBankAccounts;
}