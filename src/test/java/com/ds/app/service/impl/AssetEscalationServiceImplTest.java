package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ds.app.dto.AssetEscalationDTO;
import com.ds.app.dto.RaiseEscalationRequestDTO;
import com.ds.app.entity.Asset;
import com.ds.app.entity.AssetEscalation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.iAppUserRepository;

@ExtendWith(MockitoExtension.class)
class AssetEscalationServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetEscalationRepository assetEscalationRepository;

    @Mock
    private iAppUserRepository appUserRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AssetEscalationServiceImpl assetEscalationService;

    @Test
    void raiseEscalation_shouldCreateEscalationAndMarkEmployeeActive() {

        RaiseEscalationRequestDTO request = new RaiseEscalationRequestDTO();
        request.setAssetId(1L);
        request.setEmployeeId(2L);
        request.setReason("Misuse of asset");

        Asset asset = new Asset();
        asset.setAssetId(1L);

        Employee employee = new Employee();
        employee.setUserId(2L);
        employee.setHasActiveAssetEscalation(false);

        Employee hrUser = new Employee();
        hrUser.setUserId(100L);

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));
        when(appUserRepository.findById(2L))
                .thenReturn(Optional.of(employee));
        when(appUserRepository.findById(100L))
                .thenReturn(Optional.of(hrUser));
        when(assetEscalationRepository.save(any(AssetEscalation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssetEscalationDTO result =
                assetEscalationService.raiseEscalation(request, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getAssetId());
        assertEquals(2L, result.getEmployeeId());
        assertEquals("Misuse of asset", result.getReason());
        assertEquals(EscalationStatus.OPEN, result.getStatus());

        verify(employeeRepository).save(employee);
        assertTrue(employee.isHasActiveAssetEscalation());
    }

    @Test
    void raiseEscalation_shouldThrowException_whenAssetNotFound() {

        RaiseEscalationRequestDTO request = new RaiseEscalationRequestDTO();
        request.setAssetId(99L);

        when(assetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                assetEscalationService.raiseEscalation(request, 100L)
        );
    }
}