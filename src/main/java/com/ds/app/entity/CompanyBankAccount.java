package com.ds.app.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ds.app.enums.BankStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompanyBankAccount {

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
    
    @Column(nullable = false)
    private Long addedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<EmployeeBankAccount> employeeBankAccounts;
}
