package com.ds.app.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ds.app.entity.AppUser;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetService;

class AssetControllerDeleteByAdminTest {

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
    void deleteAssetByAdmin_shouldReturnSuccessMessage() throws Exception {

        Principal principal = () -> "admin_user";

        AppUser admin = new AppUser();
        admin.setUserId(99L);
        admin.setUsername("admin_user");

        when(appUserRepository.findByUsername("admin_user"))
                .thenReturn(Optional.of(admin));

        doNothing().when(assetService).deleteAsset(1L, 99L);

        mockMvc.perform(
                delete("/finsecure/admin/assets/{assetId}", 1L)
                        .principal(principal)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Asset deleted by Admin"));
    }
}
