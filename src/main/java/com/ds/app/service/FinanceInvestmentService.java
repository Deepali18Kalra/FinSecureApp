package com.ds.app.service;

import com.ds.app.dto.request.FinanceInvestmentRequestDTO;
import com.ds.app.dto.response.FinanceInvestmentResponseDTO;
import com.ds.app.enums.FundStatus;
import com.ds.app.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;

public interface FinanceInvestmentService {

    FinanceInvestmentResponseDTO addFund(FinanceInvestmentRequestDTO dto, Long addedBy) throws ResourceNotFoundException;

    FinanceInvestmentResponseDTO updateFundStatus(Long mutualFundId, FundStatus status) throws ResourceNotFoundException;

    FinanceInvestmentResponseDTO getFundById(Long empMutualFundId) throws ResourceNotFoundException;

    Page<FinanceInvestmentResponseDTO> getAllFunds(int page, int size, FundStatus status, String category) throws ResourceNotFoundException;
}
