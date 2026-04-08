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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ds.app.dto.AssetDTO;
import com.ds.app.service.AssetService;
import com.ds.app.repository.iAppUserRepository;

class AssetControllerPaginatedTest {

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
    void getAllAssetsPaginated_shouldReturnPage() throws Exception {

        AssetDTO dto = new AssetDTO();
        dto.setAssetId(1L);
        dto.setName("Laptop");

        Pageable pageable = PageRequest.of(0, 5);
        Page<AssetDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(assetService.getAllAssetsPaginated(pageable))
                .thenReturn(page);

        mockMvc.perform(
                get("/finsecure/common/assets/paginated")
                        .param("page", "0")
                        .param("size", "5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].assetId").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Laptop"))
        .andExpect(jsonPath("$.totalElements").value(1));
    }
}