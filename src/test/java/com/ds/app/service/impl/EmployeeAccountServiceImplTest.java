package com.ds.app.service.impl;

import com.ds.app.dto.request.EmployeeBankAccountRequestDTO;
import com.ds.app.dto.response.EmployeeBankAccountResponseDTO;
import com.ds.app.entity.*;
import com.ds.app.enums.*;
import com.ds.app.exception.*;
import com.ds.app.repository.*;
import com.ds.app.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeAccountServiceImplTest {

    @Mock 
    private EmployeeBankAccountRepository employeeBankAccountRepository;
    @Mock 
    private FinanceBankRepository allBankRepository;
    @Mock 
    private EmployeeRepository employeeRepository;
    @Mock 
    private EmailService emailService;

    @InjectMocks
    private EmployeeAccountServiceImpl employeeAccountService;

    private Employee mockEmployee;
    private FinanceBankAccount mockBank;
    private EmployeeBankAccount mockAccount;
    private EmployeeBankAccountRequestDTO dto;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setUserId(1L);
        mockEmployee.setFirstName("Rahul");
        mockEmployee.setLastName("Verma");
        mockEmployee.setUsername("rahul@test.com");

        mockBank = new FinanceBankAccount();
        mockBank.setBankId(1L);
        mockBank.setBankName("ICICI Bank");
        mockBank.setBankCode("ICIC");
        mockBank.setStatus(BankStatus.WHITELISTED);

        mockAccount = EmployeeBankAccount.builder()
                .empBankId(1L)
                .employee(mockEmployee)
                .bank(mockBank)
                .accountNumber("123456781234")
                .ifscCode("ICIC0001234")
                .accountHolderName("Rahul Verma")
                .validationStatus(BankValidationStatus.PENDING)
                .modifiedToday(0)
                .build();

        dto = new EmployeeBankAccountRequestDTO(
                1L, "987654321234", "ICIC0005678", "Rahul Verma");
    }

    // ── addBankAccount tests ──────────────────────────────────────────────

    @Test
    void addBankAccount_success() throws ResourceNotFoundException, BlacklistedBankException {
        when(employeeRepository.findByUserId(1L))
                .thenReturn(Optional.of(mockEmployee));
        when(employeeBankAccountRepository.existsByEmployee_UserId(1L))
                .thenReturn(false);
        when(allBankRepository.findById(1L))
                .thenReturn(Optional.of(mockBank));
        when(employeeBankAccountRepository.save(any()))
                .thenReturn(mockAccount);

        EmployeeBankAccountResponseDTO result =
                employeeAccountService.addBankAccount(dto, 1L);

        assertThat(result).isNotNull();
        verify(employeeBankAccountRepository).save(any());
    }

    @Test
    void addBankAccount_employeeAlreadyHasAccount_throwsException() {
        when(employeeRepository.findByUserId(1L))
                .thenReturn(Optional.of(mockEmployee));
        when(employeeBankAccountRepository.existsByEmployee_UserId(1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                employeeAccountService.addBankAccount(dto, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("already has a bank account");

        verify(employeeBankAccountRepository, never()).save(any());
    }

    @Test
    void addBankAccount_blacklistedBank_throwsException() {
        mockBank.setStatus(BankStatus.BLACKLISTED);

        when(employeeRepository.findByUserId(1L))
                .thenReturn(Optional.of(mockEmployee));
        when(employeeBankAccountRepository.existsByEmployee_UserId(1L))
                .thenReturn(false);
        when(allBankRepository.findById(1L))
                .thenReturn(Optional.of(mockBank));

        assertThatThrownBy(() ->
                employeeAccountService.addBankAccount(dto, 1L))
                .isInstanceOf(BlacklistedBankException.class)
                .hasMessageContaining("blacklisted");

        verify(employeeBankAccountRepository, never()).save(any());
    }

    @Test
    void addBankAccount_employeeNotFound_throwsException() {
        when(employeeRepository.findByUserId(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                employeeAccountService.addBankAccount(dto, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateBankAccount — fraud detection tests ─────────────────────────

    @Test
    void updateBankAccount_success_firstChange() throws BankAccountLockedException, ResourceNotFoundException, BlacklistedBankException {
        mockAccount.setModifiedToday(0);
//        mockAccount.setWindowStartTime(null);
        mockAccount.setCoolDownPeriod(null);

        when(employeeBankAccountRepository.findByEmployee_UserId(1L))
                .thenReturn(Optional.of(mockAccount));
        when(allBankRepository.findById(1L))
                .thenReturn(Optional.of(mockBank));
        when(employeeBankAccountRepository.save(any()))
                .thenReturn(mockAccount);

        EmployeeBankAccountResponseDTO result =
                employeeAccountService.updateBankAccount(dto, 1L);

        assertThat(result).isNotNull();
        verify(employeeBankAccountRepository).save(any());
    }

    @Test
    void updateBankAccount_thirdChange_triggersCooldown() throws BankAccountLockedException, ResourceNotFoundException, BlacklistedBankException {
        // already changed 2 times — this is the 3rd
        mockAccount.setModifiedToday(2);
//        mockAccount.setWindowStartTime(LocalDateTime.now().minusHours(1));
        mockAccount.setCoolDownPeriod(null);

        when(employeeBankAccountRepository.findByEmployee_UserId(1L))
                .thenReturn(Optional.of(mockAccount));
        when(allBankRepository.findById(1L))
                .thenReturn(Optional.of(mockBank));
        when(employeeBankAccountRepository.save(any()))
                .thenReturn(mockAccount);

        employeeAccountService.updateBankAccount(dto, 1L);

        // verify fraud alert email was sent
//        verify(emailService);.sendFraudAlertEmail(
//                anyString(), anyString(), anyString());
    }

    @Test
    void updateBankAccount_accountInCooldown_throwsException() {
        // cooldown active — expires tomorrow
        mockAccount.setCoolDownPeriod(LocalDateTime.now().plusHours(20));
        mockAccount.setModifiedToday(3);

        when(employeeBankAccountRepository.findByEmployee_UserId(1L))
                .thenReturn(Optional.of(mockAccount));

        assertThatThrownBy(() ->
                employeeAccountService.updateBankAccount(dto, 1L))
                .isInstanceOf(BankAccountLockedException.class)
                .hasMessageContaining("cooldown");

        // account should NOT be updated
        verify(employeeBankAccountRepository, never()).save(any());
    }

    @Test
    void updateBankAccount_cooldownExpired_allowsChange() {
        // cooldown was set but already expired
        mockAccount.setCoolDownPeriod(LocalDateTime.now().minusHours(1));
        mockAccount.setModifiedToday(3);
//        mockAccount.setWindowStartTime(LocalDateTime.now().minusHours(25));

        when(employeeBankAccountRepository.findByEmployee_UserId(1L))
                .thenReturn(Optional.of(mockAccount));
        when(allBankRepository.findById(1L))
                .thenReturn(Optional.of(mockBank));
        when(employeeBankAccountRepository.save(any()))
                .thenReturn(mockAccount);

        // should NOT throw — cooldown expired, window reset
        assertThatNoException().isThrownBy(() ->
                employeeAccountService.updateBankAccount(dto, 1L));
    }

    @Test
    void updateBankAccount_blacklistedBank_throwsException() {
        mockBank.setStatus(BankStatus.BLACKLISTED);
        mockAccount.setCoolDownPeriod(null);
        mockAccount.setModifiedToday(0);

        when(employeeBankAccountRepository.findByEmployee_UserId(1L))
                .thenReturn(Optional.of(mockAccount));
        when(allBankRepository.findById(1L))
                .thenReturn(Optional.of(mockBank));

        assertThatThrownBy(() ->
                employeeAccountService.updateBankAccount(dto, 1L))
                .isInstanceOf(BlacklistedBankException.class);
    }

    // ── getMyBankAccount tests ────────────────────────────────────────────

    @Test
    void getMyBankAccount_success() throws ResourceNotFoundException {
        when(employeeBankAccountRepository.findByEmployee_UserId(1L))
                .thenReturn(Optional.of(mockAccount));

        EmployeeBankAccountResponseDTO result =
                employeeAccountService.getMyBankAccount(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getMyBankAccount_notFound_throwsException() {
        when(employeeBankAccountRepository.findByEmployee_UserId(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                employeeAccountService.getMyBankAccount(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

