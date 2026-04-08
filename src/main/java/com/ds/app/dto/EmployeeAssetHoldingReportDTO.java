package com.ds.app.dto;

import java.time.LocalDate;
import java.util.List;

public class EmployeeAssetHoldingReportDTO {

    private Long employeeId;
    private List<HeldAssetDetail> assetsHeld;

    public static class HeldAssetDetail {
        private Long assetId;
        private String assetName;
        private String category;
        private LocalDate allocatedDate;

        public Long getAssetId() {
            return assetId;
        }

        public void setAssetId(Long assetId) {
            this.assetId = assetId;
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public LocalDate getAllocatedDate() {
            return allocatedDate;
        }

        public void setAllocatedDate(LocalDate allocatedDate) {
            this.allocatedDate = allocatedDate;
        }
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public List<HeldAssetDetail> getAssetsHeld() {
        return assetsHeld;
    }

    public void setAssetsHeld(List<HeldAssetDetail> assetsHeld) {
        this.assetsHeld = assetsHeld;
    }
}