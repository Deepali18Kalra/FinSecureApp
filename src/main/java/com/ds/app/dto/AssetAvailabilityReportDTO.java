package com.ds.app.dto;

import java.time.LocalDate;
import java.util.List;

public class AssetAvailabilityReportDTO {

    private Long assetId;
    private String assetName;
    private String category;

    private Integer totalUnits;
    private Integer allocatedUnits;
    private Integer availableUnits;

    private List<AllocationDetail> allocatedTo;

    public static class AllocationDetail {
        private Long employeeId;
        private LocalDate allocatedDate;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public LocalDate getAllocatedDate() {
            return allocatedDate;
        }

        public void setAllocatedDate(LocalDate allocatedDate) {
            this.allocatedDate = allocatedDate;
        }
    }

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

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Integer getAllocatedUnits() {
        return allocatedUnits;
    }

    public void setAllocatedUnits(Integer allocatedUnits) {
        this.allocatedUnits = allocatedUnits;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public List<AllocationDetail> getAllocatedTo() {
        return allocatedTo;
    }

    public void setAllocatedTo(List<AllocationDetail> allocatedTo) {
        this.allocatedTo = allocatedTo;
    }
}
