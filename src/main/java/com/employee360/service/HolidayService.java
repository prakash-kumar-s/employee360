package com.employee360.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.employee360.entity.Holiday;
import com.employee360.repository.HolidayRepository;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(
            HolidayRepository holidayRepository) {

        this.holidayRepository =
                holidayRepository;
    }

    public List<Holiday> getAllHolidays() {

        return holidayRepository.findAll();
    }

    public Holiday getHoliday(Long id) {

        return holidayRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Holiday not found."
                        )
                );
    }

    public Holiday createHoliday(
            String name,
            LocalDate date) {

        if (holidayRepository.existsByDate(date)) {

            throw new IllegalArgumentException(
                    "A holiday already exists for this date."
            );
        }

        Holiday holiday =
                new Holiday();

        holiday.setName(name.trim());
        holiday.setDate(date);

        return holidayRepository.save(
                holiday
        );
    }

    public Holiday updateHoliday(
            Long id,
            String name,
            LocalDate date) {

        Holiday holiday =
                getHoliday(id);

        Holiday existing =
                holidayRepository.findByDate(date);

        if (existing != null
                && !existing.getId().equals(id)) {

            throw new IllegalArgumentException(
                    "A holiday already exists for this date."
            );
        }

        holiday.setName(name.trim());
        holiday.setDate(date);

        return holidayRepository.save(
                holiday
        );
    }

    public void deleteHoliday(Long id) {

        Holiday holiday =
                getHoliday(id);

        holidayRepository.delete(holiday);
    }
}