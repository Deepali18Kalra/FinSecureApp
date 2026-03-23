package com.ds.app.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.ds.app.enums.BankValidationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EmployeeBankAccount {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "employee_id", nullable = false, unique = true)
    private Long employeeId;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private CompanyBankAccount bank;

    
    @Column(nullable = false, length = 30)
    private String accountNumber;

    @Column(nullable = false, length = 15)
    private String ifscCode;

    @Column(nullable = false, length = 100)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankValidationStatus validationStatus = BankValidationStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<SalaryRecord> salaryRecords;
}
