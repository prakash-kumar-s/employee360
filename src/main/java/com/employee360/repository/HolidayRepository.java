package com.employee360.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.Holiday;

public interface HolidayRepository
        extends JpaRepository<Holiday, Long> {

    Holiday findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);
}