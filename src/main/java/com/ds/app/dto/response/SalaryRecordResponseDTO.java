package com.ds.app.dto.response;

import com.ds.app.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRecordResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeName;           // resolved from User
    private String salaryMonth;            // "2025-03" format
    private Double grossSalary;
    private Double deductions;
    private Double netSalary;
    private String bankAccountMasked;      // ****1234 — from EmployeeBankAccount
    private String bankName;               // resolved from BankMaster
    private PaymentStatus paymentStatus;
    private LocalDateTime creditedAt;
}
