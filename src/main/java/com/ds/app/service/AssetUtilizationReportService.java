package com.ds.app.service;

import com.ds.app.dto.AssetUtilizationReportDTO;

public interface AssetUtilizationReportService {

	AssetUtilizationReportDTO getAssetUtilization(Long assetId);
}
