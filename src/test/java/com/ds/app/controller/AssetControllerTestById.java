package com.ds.app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ds.app.dto.AssetDTO;
import com.ds.app.service.AssetService;
import com.ds.app.repository.iAppUserRepository;

class AssetControllerTestById {

    private MockMvc mockMvc;

    @Mock
    private AssetService assetService;

    @Mock
    private iAppUserRepository appUserRepository;

    @InjectMocks
    private AssetController assetController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(assetController)
                .build();
    }

    @Test
    void getAssetById_shouldReturnAsset() throws Exception {

        AssetDTO dto = new AssetDTO();
        dto.setAssetId(5L);
        dto.setName("Monitor");

        when(assetService.getAssetById(5L))
                .thenReturn(dto);

        mockMvc.perform(
                get("/finsecure/common/assets/{assetId}", 5L)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assetId").value(5))
        .andExpect(jsonPath("$.name").value("Monitor"));
    }
}
