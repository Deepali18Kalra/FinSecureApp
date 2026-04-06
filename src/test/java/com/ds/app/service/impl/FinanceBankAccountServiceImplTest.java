package com.ds.app.service.impl;

import com.ds.app.dto.request.FinanceBankAccountRequestDTO;
import com.ds.app.dto.response.FinanceBankAccountResponseDTO;
import com.ds.app.entity.FinanceBankAccount;
import com.ds.app.enums.BankStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.FinanceBankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceBankAccountServiceImplTest {

    @Mock
    private FinanceBankRepository allBankRepository;

    @InjectMocks
    private FinanceBankAccountServiceImpl bankService;

    private FinanceBankAccount mockBank;

    @BeforeEach
    void setUp() {
        mockBank = new FinanceBankAccount();
        mockBank.setBankId(1L);
        mockBank.setBankName("ICICI Bank");
        mockBank.setBankCode("ICIC");
        mockBank.setStatus(BankStatus.WHITELISTED);
        mockBank.setAddedBy(1L);
        mockBank.setCreatedAt(LocalDateTime.now());
    }

    // ── addBank tests ─────────────────────────────────────────────────────

    @Test
    void addBank_success() throws ResourceNotFoundException {
    	FinanceBankAccountRequestDTO dto = new FinanceBankAccountRequestDTO(
                "ICICI Bank", "ICIC", BankStatus.WHITELISTED);

        when(allBankRepository.existsBybankCode("ICIC")).thenReturn(false);
        when(allBankRepository.save(any())).thenReturn(mockBank);

        FinanceBankAccountResponseDTO result = bankService.addBank(dto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getBankName()).isEqualTo("ICICI Bank");
        assertThat(result.getBankCode()).isEqualTo("ICIC");
        assertThat(result.getStatus()).isEqualTo(BankStatus.WHITELISTED);
        verify(allBankRepository, times(1)).save(any());
    }

    @Test
    void addBank_duplicateBankCode_throwsException() {
    	FinanceBankAccountRequestDTO dto = new FinanceBankAccountRequestDTO(
                "ICICI Bank", "ICIC", BankStatus.WHITELISTED);

        when(allBankRepository.existsBybankCode("ICIC")).thenReturn(true);

        assertThatThrownBy(() -> bankService.addBank(dto, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ICIC");

        verify(allBankRepository, never()).save(any());
    }

    // ── updateBankStatus tests ────────────────────────────────────────────

    @Test
    void updateBankStatus_success() throws ResourceNotFoundException {
        when(allBankRepository.findById(1L)).thenReturn(Optional.of(mockBank));
        when(allBankRepository.save(any())).thenReturn(mockBank);

        FinanceBankAccountResponseDTO result =
                bankService.updateBankStatus(1L, BankStatus.BLACKLISTED);

        assertThat(result).isNotNull();
        verify(allBankRepository, times(1)).save(any());
    }

    @Test
    void updateBankStatus_notFound_throwsException() {
        when(allBankRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.updateBankStatus(99L, BankStatus.BLACKLISTED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    

    // ── getBankById tests ─────────────────────────────────────────────────

    @Test
    void getBankById_success() throws ResourceNotFoundException {
        when(allBankRepository.findById(1L)).thenReturn(Optional.of(mockBank));

        FinanceBankAccountResponseDTO result = bankService.getBankById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getBankId()).isEqualTo(1L);
        assertThat(result.getBankName()).isEqualTo("ICICI Bank");
    }

    @Test
    void getBankById_notFound_throwsException() {
        when(allBankRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.getBankById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getAllBanks tests ─────────────────────────────────────────────────

    @Test
    void getAllBanks_noFilter_returnsPage() {
        Page<FinanceBankAccount> mockPage =
                new PageImpl<>(List.of(mockBank));

        when(allBankRepository.findAll(any(Pageable.class)))
                .thenReturn(mockPage);

        Page<FinanceBankAccountResponseDTO> result =
                bankService.getAllBanks(0, 10, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBankName())
                .isEqualTo("ICICI Bank");
    }

    @Test
    void getAllBanks_withStatusFilter_returnsFilteredPage() {
        Page<FinanceBankAccount> mockPage =
                new PageImpl<>(List.of(mockBank));

        when(allBankRepository.findByStatus(eq(BankStatus.WHITELISTED),
                any(Pageable.class))).thenReturn(mockPage);

        Page<FinanceBankAccountResponseDTO> result =
                bankService.getAllBanks(0, 10, BankStatus.WHITELISTED);

        assertThat(result.getContent()).hasSize(1);
        verify(allBankRepository, never()).findAll(any(Pageable.class));
    }
}





