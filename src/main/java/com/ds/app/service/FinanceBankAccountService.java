package com.ds.app.service;

import com.ds.app.dto.request.FinanceBankAccountRequestDTO;
import com.ds.app.dto.response.FinanceBankAccountResponseDTO;
import com.ds.app.enums.BankStatus;
import com.ds.app.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;

public interface FinanceBankAccountService {

    FinanceBankAccountResponseDTO addBank(FinanceBankAccountRequestDTO dto, Long addedBy) throws ResourceNotFoundException;

    FinanceBankAccountResponseDTO updateBankStatus(Long bankId, BankStatus status) throws ResourceNotFoundException;

    FinanceBankAccountResponseDTO getBankById(Long bankId) throws ResourceNotFoundException;

    Page<FinanceBankAccountResponseDTO> getAllBanks(int page, int size, BankStatus status);

}
