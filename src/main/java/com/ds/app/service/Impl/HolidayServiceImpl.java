package com.ds.app.service.Impl;

import com.ds.app.dto.HolidayRequest;
import com.ds.app.dto.HolidayResponse;
import com.ds.app.entity.Holiday;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.HolidayMapper;
import com.ds.app.repository.IHolidayRepository;
import com.ds.app.service.IHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements IHolidayService {

    private final IHolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    @Override
    @Transactional
    public HolidayResponse createHoliday(HolidayRequest request) {

        // check if a holiday already exists on this date
        if (holidayRepository.existsByDate(request.getDate())) {
            throw new IllegalStateException(
                    "A holiday already exists on date: " + request.getDate());
        }

        Holiday holiday = holidayMapper.mapToEntity(request);
        Holiday saved = holidayRepository.save(holiday);
        return holidayMapper.mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteHoliday(Long holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Holiday not found with id: " + holidayId));

        holidayRepository.delete(holiday);
    }

    @Override
    public List<HolidayResponse> getHolidaysByYear(Integer year) {

        // default to current year if not provided
        int targetYear = (year != null) ? year : Year.now().getValue();

        return holidayRepository.findByYear(targetYear)
                .stream()
                .map(holidayMapper::mapToResponse)
                .toList();
    }
}