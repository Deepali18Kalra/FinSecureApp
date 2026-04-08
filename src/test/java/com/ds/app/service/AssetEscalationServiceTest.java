package com.ds.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ds.app.dto.AssetEscalationDTO;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetEscalation;
import com.ds.app.entity.Employee;
import com.ds.app.entity.AppUser;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.service.impl.AssetEscalationServiceImpl;

@ExtendWith(MockitoExtension.class)
class AssetEscalationServiceTest {

    @Mock
    private AssetEscalationRepository assetEscalationRepository;

    @InjectMocks
    private AssetEscalationServiceImpl assetEscalationService;

    @Test
    void getAllEscalations_shouldReturnDtoList() {

        Asset asset = new Asset();
        asset.setAssetId(1L);

        Employee employee = new Employee();
        employee.setUserId(2L);

        AppUser raisedBy = new AppUser();
        raisedBy.setUserId(3L);

        AssetEscalation escalation = new AssetEscalation();
        escalation.setEscalationId(10L);
        escalation.setAsset(asset);
        escalation.setEmployee(employee);
        escalation.setRaisedBy(raisedBy);
        escalation.setReason("Late return");
        escalation.setStatus(EscalationStatus.OPEN);
        escalation.setCreatedAt(LocalDateTime.now());

        when(assetEscalationRepository.findAll())
                .thenReturn(List.of(escalation));

        List<AssetEscalationDTO> result =
                assetEscalationService.getAllEscalations();

        assertThat(result).hasSize(1);

        AssetEscalationDTO dto = result.get(0);
        assertThat(dto.getEscalationId()).isEqualTo(10L);
        assertThat(dto.getAssetId()).isEqualTo(1L);
        assertThat(dto.getEmployeeId()).isEqualTo(2L);
        assertThat(dto.getRaisedBy()).isEqualTo(3L);
        assertThat(dto.getReason()).isEqualTo("Late return");
        assertThat(dto.getStatus()).isEqualTo(EscalationStatus.OPEN);
        assertThat(dto.getCreatedAt()).isNotNull();
    }
}