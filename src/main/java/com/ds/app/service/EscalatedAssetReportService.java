package com.ds.app.service;

import java.util.List;

import com.ds.app.dto.EscalatedAssetReportDTO;

public interface EscalatedAssetReportService {

	List<EscalatedAssetReportDTO> getEscalationAwareAssetReport(Long assetId);
}
