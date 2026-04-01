package com.ds.app.service.impl;
import com.ds.app.dto.request.FinanceInvestmentRequestDTO;
import com.ds.app.dto.response.FinanceInvestmentResponseDTO;
import com.ds.app.entity.FinanceInvestment;
import com.ds.app.enums.FundStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.FinanceInvestmentRepository;
import com.ds.app.service.FinanceInvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
@Service
public class FinanceInvestmentServiceImpl implements FinanceInvestmentService {
    @Autowired
    FinanceInvestmentRepository mutualFundRepository;

    @Override
    public FinanceInvestmentResponseDTO addFund(FinanceInvestmentRequestDTO dto, Long addedBy) throws ResourceNotFoundException {

        if (mutualFundRepository.existsByFundCode(dto.getFundCode())) {
            throw new ResourceNotFoundException("Fund with code " + dto.getFundCode() + " already exists");
        }

        if (mutualFundRepository.existsByFundName(dto.getFundName())) {
            throw new ResourceNotFoundException("Fund with name " + dto.getFundName() + " already exists");
        }

        FinanceInvestment fund = FinanceInvestment.builder()
                .fundName(dto.getFundName())
                .fundCode(dto.getFundCode())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .addedBy(addedBy)
                .build();

        return mapToResponse(mutualFundRepository.save(fund));
    }

    @Override
    public FinanceInvestmentResponseDTO updateFundStatus(Long id, FundStatus status) throws ResourceNotFoundException {

        FinanceInvestment fund = mutualFundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fund not found with id: " + id));

        fund.setStatus(status);
        return mapToResponse(mutualFundRepository.save(fund));
    }

    @Override
    public FinanceInvestmentResponseDTO getFundById(Long id) throws ResourceNotFoundException {

        FinanceInvestment fund = mutualFundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fund not found with id: " + id));

        return mapToResponse(fund);
    }

    @Override
    public Page<FinanceInvestmentResponseDTO> getAllFunds(int page, int size, FundStatus status, String category) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // filter by status only
        if (status != null && category == null) {
            return mutualFundRepository.findByStatus(status, pageable)
                    .map(this::mapToResponse);
        }

        // filter by category only
        if (category != null && status == null) {
            return mutualFundRepository.findByCategory(category, pageable)
                    .map(this::mapToResponse);
        }

        // filter by both status and category
        if (status != null && category != null) {
            return mutualFundRepository.findByStatusAndCategory(status, category, pageable)
                    .map(this::mapToResponse);
        }

        // no filter — return all
        return mutualFundRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private FinanceInvestmentResponseDTO mapToResponse(FinanceInvestment fund) {
        FinanceInvestmentResponseDTO dto = new FinanceInvestmentResponseDTO();
        dto.setMutualFundId	(fund.getMutualFundId());
        dto.setFundName(fund.getFundName());
        dto.setFundCode(fund.getFundCode());
        dto.setCategory(fund.getCategory());
        dto.setStatus(fund.getStatus());
        dto.setAddedBy(fund.getAddedBy());
        dto.setCreatedAt(fund.getCreatedAt());
        dto.setUpdatedAt(fund.getUpdatedAt());
        return dto;
    }
}
