package com.ds.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.PageRequest;

import com.ds.app.dto.AssetDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.UserRole;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/finsecure")

@Tag(
    name = "Asset Management",
    description = "APIs related to asset creation, update, deletion, and retrieval"
)
public class AssetController {

    private final AssetService assetService;
    private final iAppUserRepository appUserRepository;

    public AssetController(AssetService assetService,
                           iAppUserRepository appUserRepository) {
        this.assetService = assetService;
        this.appUserRepository = appUserRepository;
    }


    @PostMapping("/hr/assets")
    @PreAuthorize("hasAuthority('HR')")
    public AssetDTO createAssetByHr(
            @Valid @RequestBody AssetDTO dto,Principal principal) {
        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return assetService.createAsset(dto, user.getUserId());
    }

    @PutMapping("/hr/assets/{assetId}")
    @PreAuthorize("hasAuthority('HR')")
    public AssetDTO updateAssetByHr(
            @PathVariable Long assetId,
            @RequestBody AssetDTO dto,
            Principal principal) {

        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return assetService.updateAsset(assetId, dto, user.getUserId());
    }

    
    @DeleteMapping("/hr/assets/{assetId}")
    @PreAuthorize("hasAuthority('HR')")
    public String deleteAssetByHr(
            @PathVariable Long assetId,
            Principal principal) {

        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assetService.deleteAsset(assetId, user.getUserId());
        return "Asset deleted by HR";
    }
    
    
    //admin
    @PostMapping("/admin/assets")
    @PreAuthorize("hasAuthority('ADMIN')")
    public AssetDTO createAssetByAdmin(
            @Valid @RequestBody AssetDTO dto,
            Principal principal) {

        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return assetService.createAsset(dto, user.getUserId());
    }

    @PutMapping("/admin/assets/{assetId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public AssetDTO updateAssetByAdmin(
            @PathVariable Long assetId,
            @RequestBody AssetDTO dto,
            Principal principal) {

        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return assetService.updateAsset(assetId, dto, user.getUserId());
    }

    @DeleteMapping("/admin/assets/{assetId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteAssetByAdmin(
            @PathVariable Long assetId,
            Principal principal) {

        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assetService.deleteAsset(assetId, user.getUserId());
        return "Asset deleted by Admin";
    }

    @GetMapping("/common/assets")
    public List<AssetDTO> getAllAssets() {
        return assetService.getAllAssets();
    }

    @GetMapping("/common/assets/{assetId}")
    public AssetDTO getAsset(@PathVariable Long assetId) {
        return assetService.getAssetById(assetId);
    }
    
    @GetMapping("/common/assets/paginated")
    public Page<AssetDTO> getAllAssetsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
	{
		Pageable pageable=PageRequest.of(page, size);
		return assetService.getAllAssetsPaginated(pageable);
		
	}
}
