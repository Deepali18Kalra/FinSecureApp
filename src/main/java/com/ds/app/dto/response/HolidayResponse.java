package com.ds.app.dto.response;

import com.ds.app.enums.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayResponse {
    private Long holidayId;
    private LocalDate date;
    private String name;
    private HolidayType type;
}