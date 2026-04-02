package com.ds.app.controller;
import com.ds.app.dto.request.FinanceInvestmentRequestDTO;
import com.ds.app.dto.response.FinanceInvestmentResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.enums.FundStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.service.FinanceInvestmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finsecure/finance/mutual-funds")
public class FinanceInvestmentController {

    @Autowired
    FinanceInvestmentService companyMutualFundService;

    private Long getLoggedInUserId() {
        MyUserDetails userDetails= (MyUserDetails) SecurityContextHolder.getContext()
 				.getAuthentication().getPrincipal();
        return userDetails.getUser().getUserId();
     }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE')")
    public FinanceInvestmentResponseDTO addFund(
            @Valid @RequestBody FinanceInvestmentRequestDTO dto) throws ResourceNotFoundException {
        return companyMutualFundService.addFund(dto,getLoggedInUserId());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('FINANCE')")
    public FinanceInvestmentResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam FundStatus status) throws ResourceNotFoundException {
        return companyMutualFundService.updateFundStatus(id, status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE')")
    public FinanceInvestmentResponseDTO getById(@PathVariable Long id) throws ResourceNotFoundException {
        return companyMutualFundService.getFundById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE')")
    public List<FinanceInvestmentResponseDTO> getAll(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(required = false)     FundStatus status,
            @RequestParam(required = false)     String category) throws ResourceNotFoundException {
        return companyMutualFundService.getAllFunds(page, size, status, category).getContent();
    }



}
