package com.ds.app.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ds.app.dto.AssetIssueDTO;
import com.ds.app.dto.RaiseIssueRequestDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetIssueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/finsecure")
public class AssetIssueController {

    private final AssetIssueService issueService;
    private final iAppUserRepository appUserRepository;

    public AssetIssueController(
            AssetIssueService issueService,
            iAppUserRepository appUserRepository) {
        this.issueService = issueService;
        this.appUserRepository = appUserRepository;
    }

    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @PostMapping("/employees/issues")
    public AssetIssueDTO raiseIssueByEmployee(
            @Valid @RequestBody RaiseIssueRequestDTO request,
            Principal principal) {

        AppUser user = appUserRepository
                .findByUsername(principal.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return issueService.raiseIssue(request, user.getUserId());
    }

    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/hr/issues/{issueId}")
    public AssetIssueDTO updateIssueStatusByHr(
            @PathVariable Long issueId,
            @RequestParam String status,
            Principal principal) {

        AppUser user = appUserRepository
                .findByUsername(principal.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return issueService.updateIssueStatus(issueId, status, user.getUserId());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/issues/{issueId}")
    public AssetIssueDTO updateIssueStatusByAdmin(
            @PathVariable Long issueId,
            @RequestParam String status,
            Principal principal) {

        AppUser user = appUserRepository
                .findByUsername(principal.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return issueService.updateIssueStatus(issueId, status, user.getUserId());
    }

    @GetMapping("/common/issues/paginated")
    public Page<AssetIssueDTO> getAllIssuesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return issueService.getAllIssuesPaginated(pageable);
    }
}
