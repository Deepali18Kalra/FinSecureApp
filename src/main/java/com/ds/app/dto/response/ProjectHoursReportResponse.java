package com.ds.app.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectHoursReportResponse {
    private Long projectId;
    private double totalProjectHours;
    private List<EmployeeProjectHoursRow> employeeWiseRows;
}