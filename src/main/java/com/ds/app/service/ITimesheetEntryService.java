package com.ds.app.service;

import java.time.LocalDate;
import java.util.List;

import com.ds.app.dto.TimesheetEntryRequest;
import com.ds.app.dto.TimesheetEntryResponse;

public interface ITimesheetEntryService {
    TimesheetEntryResponse addMyEntry(TimesheetEntryRequest request);
    List<TimesheetEntryResponse> getMyEntries(Integer month, Integer year);
    List<TimesheetEntryResponse> getMyEntriesByDateRange(LocalDate startDate, LocalDate endDate);
    TimesheetEntryResponse updateMyEntry(Long entryId, TimesheetEntryRequest request);
    void deleteMyEntry(Long entryId);
}