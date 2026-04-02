package com.ds.app.utils;

import com.ds.app.exception.InvalidDateRangeException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

public class DateUtil {

    public static int totalDaysInclusive(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        return (int) (java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    public static int workingDaysExcludingHolidays(LocalDate startDate, LocalDate endDate, Set<LocalDate> holidays) {
        validateRange(startDate, endDate);

        return (int) Stream.iterate(startDate, d -> !d.isAfter(endDate), d -> d.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY)
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(d -> holidays == null || !holidays.contains(d))
                .count();
    }

    public static void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date cannot be before start date");
        }
    }
}