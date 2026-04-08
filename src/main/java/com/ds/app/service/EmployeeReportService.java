package com.ds.app.service;

import com.ds.app.dto.EmployeeAssetHoldingReportDTO;

public interface EmployeeReportService {

    EmployeeAssetHoldingReportDTO getAssetsHeldByEmployee(Long employeeId);
}
