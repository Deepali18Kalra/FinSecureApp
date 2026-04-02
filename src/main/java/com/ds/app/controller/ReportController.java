package com.ds.app.controller;

import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/finsecure/finance/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // Report 1 — salary register
    // GET /finsecure/finance/reports/salary?month=2025-03&page=0&size=10
    @GetMapping("/salary")
    @PreAuthorize("hasAuthority('FINANCE')")
    public Page<SalaryRecordResponseDTO> getSalaryRegister(
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return reportService.getSalaryRegister(month, page, size);
    }

    // Report 2 — compliance report
    // GET /finsecure/finance/reports/compliance?page=0&size=10
    @GetMapping("/compliance")
    @PreAuthorize("hasAuthority('FINANCE')")
    public Page<EmployeeInvestmentResponseDTO> getComplianceReport(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return reportService.getComplianceReport(page, size);
    }

    // Report 3 — bank account status summary
    // GET /finsecure/finance/reports/bank-summary
    @GetMapping("/bank-summary")
    @PreAuthorize("hasAuthority('FINANCE')")
    public Map<String, Long> getBankAccountStatusReport() {
        return reportService.getBankAccountStatusReport();
    }

    // Report 4 — salary job history
    // GET /finsecure/finance/reports/salary-jobs?page=0&size=10
    @GetMapping("/salary-jobs")
    @PreAuthorize("hasAuthority('FINANCE')")
    public Page<SalaryJobResponseDTO> getSalaryJobReport(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return reportService.getSalaryJobReport(page, size);
    }

    // Report 5 — employee financial summary
    // GET /finsecure/finance/reports/employee/1
   
}