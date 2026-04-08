package com.ds.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ds.app.dto.EscalationReportDTO;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.service.impl.AssetReportServiceImpl;

@ExtendWith(MockitoExtension.class)
class AssetReportServiceTest {

    @Mock
    private AssetEscalationRepository assetEscalationRepository;

    @InjectMocks
    private AssetReportServiceImpl assetReportService;

    @Test
    void getEscalationsOnDate_shouldReturnReport() {

        EscalationReportDTO dto =
                new EscalationReportDTO(
                        1L,
                        "Laptop",
                        LocalDateTime.now(),
                        "Damaged",
                        EscalationStatus.OPEN,
                        2L,
                        "employee01"
                );

        when(assetEscalationRepository.findEscalationsOnDate(
                any(LocalDate.class)))
                .thenReturn(List.of(dto));

        List<EscalationReportDTO> result =
                assetReportService.getEscalationsOnDate(
                        LocalDate.of(2026, 4, 1)
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssetName()).isEqualTo("Laptop");
    }
}