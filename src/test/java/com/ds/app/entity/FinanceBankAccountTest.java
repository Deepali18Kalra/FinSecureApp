package com.ds.app.entity;

import com.ds.app.enums.BankStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class FinanceBankAccountTest {

    @Test
    void shouldCreateFinanceBankAccountWithAllFields() {

        LocalDateTime now = LocalDateTime.now();

        FinanceBankAccount bank = new FinanceBankAccount();
        bank.setBankId(1L);
        bank.setBankName("ICICI Bank");
        bank.setBankCode("ICIC");
        bank.setStatus(BankStatus.WHITELISTED);
        bank.setAddedBy(100L);
        bank.setCreatedAt(now);
        bank.setUpdatedAt(now);
        bank.setEmployeeBankAccounts(new ArrayList<>());

        assertThat(bank.getBankId()).isEqualTo(1L);
        assertThat(bank.getBankName()).isEqualTo("ICICI Bank");
        assertThat(bank.getBankCode()).isEqualTo("ICIC");
        assertThat(bank.getStatus()).isEqualTo(BankStatus.WHITELISTED);
        assertThat(bank.getAddedBy()).isEqualTo(100L);
        assertThat(bank.getCreatedAt()).isEqualTo(now);
        assertThat(bank.getUpdatedAt()).isEqualTo(now);
        assertThat(bank.getEmployeeBankAccounts()).isEmpty();
    }

    @Test
    void shouldHandleStatusEnumCorrectly() {

        FinanceBankAccount bank = new FinanceBankAccount();
        bank.setStatus(BankStatus.BLACKLISTED);

        assertThat(bank.getStatus()).isEqualTo(BankStatus.BLACKLISTED);
    }

    @Test
    void employeeBankAccountsListShouldBeMutable() {

        FinanceBankAccount bank = new FinanceBankAccount();
        EmployeeBankAccount account = new EmployeeBankAccount();

        bank.setEmployeeBankAccounts(new ArrayList<>());
        bank.getEmployeeBankAccounts().add(account);

        assertThat(bank.getEmployeeBankAccounts()).hasSize(1);
        assertThat(bank.getEmployeeBankAccounts().get(0))
                .isEqualTo(account);
    }

    @Test
    void defaultObjectShouldHaveNullFields() {

        FinanceBankAccount bank = new FinanceBankAccount();

        assertThat(bank.getBankId()).isNull();
        assertThat(bank.getBankName()).isNull();
        assertThat(bank.getBankCode()).isNull();
        assertThat(bank.getStatus()).isNull();
        assertThat(bank.getAddedBy()).isNull();
        assertThat(bank.getCreatedAt()).isNull();
        assertThat(bank.getUpdatedAt()).isNull();
        assertThat(bank.getEmployeeBankAccounts()).isNull();
    }
}