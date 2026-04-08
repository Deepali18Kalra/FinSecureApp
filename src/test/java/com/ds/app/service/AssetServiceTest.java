package com.ds.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ds.app.dto.AssetDTO;
import com.ds.app.entity.Asset;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetRepository;
import com.ds.app.service.impl.AssetServiceImpl;


@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetServiceImpl assetService;

    @Test
    void getAllAssets_shouldReturnList() {
        Asset asset = new Asset();
        asset.setName("Laptop");

        when(assetRepository.findAll())
                .thenReturn(List.of(asset));

        List<AssetDTO> result = assetService.getAllAssets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void getAllAssets_shouldReturnEmptyList() {
        when(assetRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<AssetDTO> result = assetService.getAllAssets();

        assertThat(result).isEmpty();
    }

    @Test
    void getAssetById_shouldReturnAsset() {
        Asset asset = new Asset();
        asset.setAssetId(1L);
        asset.setName("Monitor");

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        AssetDTO result = assetService.getAssetById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Monitor");
    }

    @Test
    void getAssetById_shouldThrowException_whenNotFound() {
        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.getAssetById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Asset");
    }
}