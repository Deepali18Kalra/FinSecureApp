package com.ds.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.dto.AssetAvailabilityReportDTO;
import com.ds.app.dto.EscalationReportDTO;

public interface AssetReportService {

    AssetAvailabilityReportDTO getAssetAvailabilityReport(Long assetId);

    List<EscalationReportDTO> getEscalationsOnDate(LocalDate date);
    
    List<EscalationReportDTO> getEscalationsBetweenDates(LocalDateTime from,LocalDateTime to);
    
    List<EscalationReportDTO> getEscalationsForEmployee(Long employeeId);

}
