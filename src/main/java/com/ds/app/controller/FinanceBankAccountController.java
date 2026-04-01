package com.ds.app.controller;
import com.ds.app.dto.request.FinanceBankAccountRequestDTO;
import com.ds.app.dto.response.FinanceBankAccountResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.enums.BankStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.service.FinanceBankAccountService;
import com.ds.app.service.MyUserDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finsecure/finance/banks")
public class FinanceBankAccountController {

    private final MyUserDetailService myUserDetailService;

    @Autowired
    FinanceBankAccountService companyBankAccountService;

    FinanceBankAccountController(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    private Long getLoggedInUserId() {
       MyUserDetails userDetails= (MyUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
       return userDetails.getUser().getUserId();
    }

    @PostMapping
    public ResponseEntity<FinanceBankAccountResponseDTO>addBank(@Valid @RequestBody FinanceBankAccountRequestDTO dto) throws ResourceNotFoundException
    {
    	
        FinanceBankAccountResponseDTO response = companyBankAccountService.addBank(dto, getLoggedInUserId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public FinanceBankAccountResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam BankStatus status) throws ResourceNotFoundException {
        return companyBankAccountService.updateBankStatus(id, status);
    }

    @GetMapping("/{id}")
    public FinanceBankAccountResponseDTO getById(@PathVariable Long id) throws ResourceNotFoundException {
        return companyBankAccountService.getBankById(id);
    }

    @GetMapping
    public List<FinanceBankAccountResponseDTO> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    BankStatus status) {

        return companyBankAccountService.getAllBanks(page, size, status).getContent();
    }



}
