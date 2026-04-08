package com.ds.app.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.AssetEscalationDTO;
import com.ds.app.dto.RaiseEscalationRequestDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.UserRole;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetEscalationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/finsecure")
public class AssetEscalationController {

	private final AssetEscalationService escalationService;
	private final iAppUserRepository appUserRepository;
	
	public AssetEscalationController(AssetEscalationService escalationService, iAppUserRepository appUserRepository) {
		super();
		this.escalationService = escalationService;
		this.appUserRepository = appUserRepository;
	}
		
	@PreAuthorize("hasAuthority('HR')")
	@PostMapping("/hr/escalations")
	public AssetEscalationDTO raiseEscalationByHr(@Valid@RequestBody RaiseEscalationRequestDTO request,Principal principal)
	{
		AppUser currentUser = appUserRepository.findByUsername(principal.getName())
		                .orElseThrow(() ->
		                        new ResourceNotFoundException("User not found"));

		        return escalationService.raiseEscalation(
		                request,
		                currentUser.getUserId()
		        );

	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/escalations")
	public AssetEscalationDTO raiseEscalationByAdmin(@Valid@RequestBody RaiseEscalationRequestDTO request,Principal principal)
	{
		AppUser currentUser = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return escalationService.raiseEscalation(
                request,
                currentUser.getUserId()
        );
	}
	
	@PreAuthorize("hasAuthority('HR')")
	@PutMapping("/hr/escalations/{escalationId}")
	public AssetEscalationDTO updateEscalationStatusByHr(@PathVariable Long escalationId, @RequestParam String status,Principal principal)
	{

		AppUser currentUser = appUserRepository
					.findByUsername(principal.getName())
					.orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return escalationService.updateEscalationStatus(
                escalationId,
                status,
                currentUser.getUserId()
        );
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/admin/escalation/{escalationId}")
	public AssetEscalationDTO updateEscalationStatusByAdmin(@PathVariable Long escalationId,@RequestParam String status,Principal principal)
	{
		AppUser currentUser = appUserRepository
				.findByUsername(principal.getName())
				.orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    return escalationService.updateEscalationStatus(
            escalationId,
            status,
            currentUser.getUserId()
    );				
}
	

	@GetMapping("/common/escalations/paginated")
	public Page<AssetEscalationDTO> getAllEscalationsPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		return escalationService.getAllEscalationsPaginated(pageable);
}

	
}
