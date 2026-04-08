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

import com.ds.app.exception.GlobalExceptionHandler;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetService;

class AssetControllerGetIdExceptionTest {

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
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAssetById_whenNotFound_shouldReturn404() throws Exception {

        when(assetService.getAssetById(50L))
                .thenThrow(new ResourceNotFoundException("Asset not found"));

        mockMvc.perform(
                get("/finsecure/common/assets/{assetId}", 50L)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Asset not found"))
        .andExpect(jsonPath("$.status").value(404));
    }
}
