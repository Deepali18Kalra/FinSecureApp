package com.ds.app.service;

import com.ds.app.dto.request.HolidayRequest;
import com.ds.app.dto.response.HolidayResponse;

import java.time.LocalDate;
import java.util.List;

public interface IHolidayService {

    // HR only
    HolidayResponse createHoliday(HolidayRequest request);

    void deleteHoliday(Long holidayId);

    // Everyone — view holidays for a year
    List<HolidayResponse> getHolidaysByYear(Integer year);

    boolean isHoliday(LocalDate date);
}