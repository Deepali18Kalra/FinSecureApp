package com.ds.app.mapper;

import com.ds.app.dto.request.HolidayRequest;
import com.ds.app.dto.response.HolidayResponse;
import com.ds.app.entity.Holiday;
import org.springframework.stereotype.Component;

@Component
public class HolidayMapper {

    public Holiday mapToEntity(HolidayRequest request) {
        return Holiday.builder()
                .date(request.getDate())
                .name(request.getName())
                .type(request.getType())
                .build();
    }

    public HolidayResponse mapToResponse(Holiday holiday) {
        return HolidayResponse.builder()
                .holidayId(holiday.getHolidayId())
                .date(holiday.getDate())
                .name(holiday.getName())
                .type(holiday.getType())
                .build();
    }
}