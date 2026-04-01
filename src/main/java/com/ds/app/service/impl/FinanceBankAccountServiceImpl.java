package com.ds.app.service.impl;
import com.ds.app.dto.request.FinanceBankAccountRequestDTO;
import com.ds.app.dto.response.FinanceBankAccountResponseDTO;
import com.ds.app.entity.FinanceBankAccount;
import com.ds.app.enums.BankStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.FinanceBankRepository;
import com.ds.app.service.FinanceBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
@Service
public class FinanceBankAccountServiceImpl implements FinanceBankAccountService {

    @Autowired
    FinanceBankRepository  allBankRepository;


    @Override
    public FinanceBankAccountResponseDTO addBank(FinanceBankAccountRequestDTO dto, Long addedBy) throws ResourceNotFoundException {

        if(allBankRepository.existsBybankCode(dto.getBankCode()))
        {
            throw new ResourceNotFoundException("Bank with BankID CODE " + dto.getBankCode() + " already exists");
        }
        FinanceBankAccount companyBankAccount = new FinanceBankAccount();
        FinanceBankAccountResponseDTO bankResponseDTO = new FinanceBankAccountResponseDTO();
        companyBankAccount.setBankName(dto.getBankName());
        companyBankAccount.setBankCode(dto.getBankCode());
        companyBankAccount.setStatus(dto.getStatus());
        companyBankAccount.setAddedBy(addedBy);

        FinanceBankAccount saved = allBankRepository.save(companyBankAccount);
        return mapToResponse(saved);
    }

    @Override
    public FinanceBankAccountResponseDTO updateBankStatus(Long id, BankStatus status) throws ResourceNotFoundException {

        FinanceBankAccount bank = allBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with id: " + id));

        bank.setStatus(status);
        return mapToResponse(allBankRepository.save(bank));
    }

    @Override
    public FinanceBankAccountResponseDTO getBankById(Long id) throws ResourceNotFoundException {
        FinanceBankAccount bank = allBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with id: " + id));

        return mapToResponse(bank);
    }

    @Override
    public Page<FinanceBankAccountResponseDTO> getAllBanks(int page, int size, BankStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<FinanceBankAccount> bankPage = (status != null)
                ? allBankRepository.findByStatus(status, pageable)
                : allBankRepository.findAll(pageable);

        return bankPage.map(this::mapToResponse);
    }


    private FinanceBankAccountResponseDTO mapToResponse(FinanceBankAccount bank)
    {
        FinanceBankAccountResponseDTO bankResponseDTO = new FinanceBankAccountResponseDTO();
        bankResponseDTO.setBankId(bank.getBankId());
        bankResponseDTO.setBankCode(bank.getBankCode());
        bankResponseDTO.setBankName(bank.getBankName());
        bankResponseDTO.setStatus(bank.getStatus());
        bankResponseDTO.setAddedBy(bank.getAddedBy());
        bankResponseDTO.setCreatedAt(bank.getCreatedAt());
        bankResponseDTO.setUpdatedAt(bank.getUpdatedAt());
        return bankResponseDTO;
    }
}
