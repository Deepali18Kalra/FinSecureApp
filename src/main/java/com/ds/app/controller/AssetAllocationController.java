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

import com.ds.app.dto.AllocateAssetRequestDTO;
import com.ds.app.dto.AssetAllocationDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.UserRole;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetAllocationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/finsecure")
public class AssetAllocationController {

	private final iAppUserRepository appUserRepository;
	private final AssetAllocationService allocationService;
	public AssetAllocationController(iAppUserRepository appUserRepository, AssetAllocationService allocationService) {
		super();
		this.appUserRepository = appUserRepository;
		this.allocationService = allocationService;
	}

	@PreAuthorize("hasAuthority('HR')")
	@PostMapping("/hr/allocations")
	public AssetAllocationDTO allocateByHr(@Valid@RequestBody AllocateAssetRequestDTO request, Principal principal)
	{
		AppUser user = appUserRepository.findByUsername(principal.getName())
		        .orElseThrow(() -> new RuntimeException("User not found"));
		Long userId = user.getUserId();
		
		
		
		return allocationService.allocateAsset(request, userId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/allocations")
	public AssetAllocationDTO allocateByAdmin(@Valid@RequestBody AllocateAssetRequestDTO request, Principal principal)
	{

		AppUser user = appUserRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    return allocationService.allocateAsset(request, user.getUserId());

	}
	
	@PutMapping("/hr/allocations/{allocationId}/deallocate")
	@PreAuthorize("hasAuthority('HR')")
	public AssetAllocationDTO deallocateByHr(
	        @PathVariable Long allocationId,
	        Principal principal) {

	    AppUser user = appUserRepository.findByUsername(principal.getName())
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    return allocationService.deallocateAsset(allocationId, user.getUserId());
	}
	
	@PutMapping("/admin/allocation/{allocationId}/deallocate")
	@PreAuthorize("hasAuthority('ADMIN')")
	public AssetAllocationDTO deallocateByAdmin(@PathVariable Long allocationId,
	        Principal principal) {

	    AppUser user = appUserRepository.findByUsername(principal.getName())
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    return allocationService.deallocateAsset(allocationId, user.getUserId());
	}
	
	
	@GetMapping("/common/allocations/paginated")
	public Page<AssetAllocationDTO> getAllAllocationsPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		Pageable pageable = PageRequest.of(page, size);
		return allocationService.getAllAllocationsPaginated(pageable);
	}

}
