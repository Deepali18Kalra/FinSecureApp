package com.ds.app.service;

import com.ds.app.dto.HolidayRequest;
import com.ds.app.dto.HolidayResponse;

import java.util.List;

public interface IHolidayService {

    // HR only
    HolidayResponse createHoliday(HolidayRequest request);

    void deleteHoliday(Long holidayId);

    // Everyone — view holidays for a year
    List<HolidayResponse> getHolidaysByYear(Integer year);
}