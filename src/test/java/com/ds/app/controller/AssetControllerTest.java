package com.ds.app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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

class AssetControllerTest {

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
    void getAllAssets_shouldReturnAssetList() throws Exception {

        AssetDTO dto = new AssetDTO();
        dto.setAssetId(1L);
        dto.setName("Laptop");

        when(assetService.getAllAssets())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/finsecure/common/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetId").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }
}
