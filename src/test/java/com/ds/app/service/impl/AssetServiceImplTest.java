package com.ds.app.service.impl;

import com.ds.app.dto.AssetDTO;
import com.ds.app.entity.Asset;
import com.ds.app.enums.AssetStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetServiceImpl assetService;

    private Asset buildAsset(Long id) {
        Asset asset = new Asset();
        asset.setAssetId(id);
        asset.setAssetTag("ASSET-" + id);
        asset.setCategory("HARDWARE");
        asset.setName("Laptop");
        asset.setCreatedBy(100L);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setTotalUnits(10);
        asset.setAllocatedUnits(2);
        asset.setAvailableUnits(8);
        return asset;
    }

    private AssetDTO buildAssetDTO() {
        AssetDTO dto = new AssetDTO();
        dto.setAssetTag("ASSET-1");
        dto.setCategory("HARDWARE");
        dto.setName("Laptop");
        dto.setTotalUnits(10);
        return dto;
    }


    @Test
    void createAsset_ShouldCreateAndReturnAssetDTO() {

        AssetDTO inputDto = buildAssetDTO();
        Asset savedAsset = buildAsset(1L);

        when(assetRepository.save(any(Asset.class)))
                .thenReturn(savedAsset);

        AssetDTO result = assetService.createAsset(inputDto, 100L);

        assertNotNull(result);
        assertEquals(1L, result.getAssetId());
        assertEquals("ASSET-1", result.getAssetTag());
        assertEquals("HARDWARE", result.getCategory());
        assertEquals("Laptop", result.getName());
        assertEquals(AssetStatus.AVAILABLE, result.getStatus());
        assertEquals(100L, result.getCreatedBy());
        assertEquals(10, result.getTotalUnits());
    }

    //found
    @Test
    void getAssetById_ShouldReturnAssetDTO() {

        Asset asset = buildAsset(1L);

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        AssetDTO result = assetService.getAssetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAssetId());
        assertEquals("Laptop", result.getName());
        assertEquals(10, result.getTotalUnits());
        assertEquals(8, result.getAvailableUnits());
    }

    //not found
    @Test
    void getAssetById_ShouldThrowException_WhenNotFound() {

        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                assetService.getAssetById(99L)
        );
    }

    @Test
    void getAllAssets_ShouldReturnListOfAssets() {

        Asset asset1 = buildAsset(1L);
        Asset asset2 = buildAsset(2L);

        when(assetRepository.findAll())
                .thenReturn(List.of(asset1, asset2));

        List<AssetDTO> result = assetService.getAllAssets();

        assertEquals(2, result.size());
        assertEquals("ASSET-1", result.get(0).getAssetTag());
        assertEquals("ASSET-2", result.get(1).getAssetTag());
    }

    @Test
    void deleteAsset_ShouldDelete_WhenAssetExists() {

        when(assetRepository.existsById(1L))
                .thenReturn(true);

        assetService.deleteAsset(1L, 100L);

        verify(assetRepository).deleteById(1L);
    }


    @Test
    void deleteAsset_ShouldThrowException_WhenNotFound() {

        when(assetRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                assetService.deleteAsset(99L, 100L)
        );
    }


    @Test
    void getAllAssetsPaginated_ShouldReturnPageOfAssets() {

        Asset asset = buildAsset(1L);
        Pageable pageable = PageRequest.of(0, 5);

        Page<Asset> page = new PageImpl<>(List.of(asset), pageable, 1);

        when(assetRepository.findAll(pageable))
                .thenReturn(page);

        Page<AssetDTO> result = assetService.getAllAssetsPaginated(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("ASSET-1", result.getContent().get(0).getAssetTag());
    }
}