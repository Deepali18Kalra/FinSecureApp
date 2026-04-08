package com.ds.app.dto;

import java.time.LocalDateTime;

import com.ds.app.enums.EscalationStatus;

public class EscalationReportDTO {

    private Long assetId;
    private String assetName;

    private LocalDateTime escalationDate;
    private String reason;
    private EscalationStatus escalationStatus;

    private Long employeeId;
    private String employeeUsername;

    public EscalationReportDTO(
            Long assetId,
            String assetName,
            LocalDateTime escalationDate,
            String reason,
            EscalationStatus escalationStatus,
            Long employeeId,
            String employeeUsername) {

        this.assetId = assetId;
        this.assetName = assetName;
        this.escalationDate = escalationDate;
        this.reason = reason;
        this.escalationStatus = escalationStatus;
        this.employeeId = employeeId;
        this.employeeUsername = employeeUsername;
    }

    
    
    
    public Long getAssetId() {
        return assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public LocalDateTime getEscalationDate() {
        return escalationDate;
    }

    public String getReason() {
        return reason;
    }

    public EscalationStatus getEscalationStatus() {
        return escalationStatus;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }
}