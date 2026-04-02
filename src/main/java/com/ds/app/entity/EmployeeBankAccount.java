package com.ds.app.entity;
import com.ds.app.enums.BankValidationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employee_bank_account")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empBankId;

    @ManyToOne(fetch = FetchType.LAZY , targetEntity = Employee.class)    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    // Many-to-One with BankMaster
    // many employees can bank with the same institution (ICICI, HDFC etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private FinanceBankAccount bank;

    // stored encrypted — never expose raw value
    @Column(nullable = false, length = 30)
    private String accountNumber;

    @Column(nullable = false, length = 15)
    private String ifscCode;

    @Column(nullable = false, length = 100)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankValidationStatus validationStatus = BankValidationStatus.PENDING;
    
    @Column(name = "review_note", length = 500)
    private String reviewNote; // HR/Admin can add comments during review
    
    @Column(name = "reviewed_by")
    private Long reviewedBy; // HR/Admin user ID who reviewed this account

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One bank account → many salary records (one per month)
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<SalaryRecord> salaryRecords;
    
    
    // trying....
    
//    private Boolean isFlagged = false;
    private Integer modifiedToday = 0;
    private LocalDateTime coolDownPeriod;
    
    


}