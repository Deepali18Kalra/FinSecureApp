package com.ds.app.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ds.app.controller.AssetController;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetService;

class GlobalExceptionHandlerTest {

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
    void whenResourceNotFound_thenReturn404AndErrorResponse() throws Exception {

        when(assetService.getAllAssets())
                .thenThrow(new ResourceNotFoundException("Asset not found"));

        mockMvc.perform(
                get("/finsecure/common/assets")
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Asset not found"))
        .andExpect(jsonPath("$.status").value(404));
    }
}
