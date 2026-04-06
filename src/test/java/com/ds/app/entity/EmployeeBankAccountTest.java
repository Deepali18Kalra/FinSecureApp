package com.ds.app.entity;

import com.ds.app.enums.BankValidationStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeBankAccountTest {

    @Test
    void builderShouldCreateValidBankAccount() {

        Employee employee = new Employee();
        FinanceBankAccount bank = new FinanceBankAccount();

        EmployeeBankAccount account = EmployeeBankAccount.builder()
                .empBankId(1L)
                .employee(employee)
                .bank(bank)
                .accountNumber("1234567890")
                .ifscCode("ICIC0001")
                .accountHolderName("Rahul Verma")
                .build();

        assertThat(account.getEmpBankId()).isEqualTo(1L);
        assertThat(account.getEmployee()).isEqualTo(employee);
        assertThat(account.getBank()).isEqualTo(bank);
        assertThat(account.getAccountNumber()).isEqualTo("1234567890");
        assertThat(account.getIfscCode()).isEqualTo("ICIC0001");
        assertThat(account.getAccountHolderName()).isEqualTo("Rahul Verma");
    }

    @Test
    void validationStatusShouldDefaultToPending() {

        EmployeeBankAccount account = new EmployeeBankAccount();

        assertThat(account.getValidationStatus())
                .isEqualTo(BankValidationStatus.PENDING);
    }

    @Test
    void modifiedTodayDefaultShouldBeZero() {

        EmployeeBankAccount account = new EmployeeBankAccount();

        assertThat(account.getModifiedToday()).isEqualTo(0);
    }
}