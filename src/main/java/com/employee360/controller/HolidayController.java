package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.HolidayRequestDto;
import com.employee360.entity.Holiday;
import com.employee360.service.HolidayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/holidays")
@PreAuthorize("hasRole('ADMIN')")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(
            HolidayService holidayService) {

        this.holidayService =
                holidayService;
    }

    @GetMapping
    public ResponseEntity<List<Holiday>> getAllHolidays() {

        return ResponseEntity.ok(
                holidayService.getAllHolidays()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Holiday> getHoliday(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                holidayService.getHoliday(id)
        );
    }

    @PostMapping
    public ResponseEntity<Holiday> createHoliday(
            @Valid
            @RequestBody HolidayRequestDto request) {

        return ResponseEntity.ok(
                holidayService.createHoliday(
                        request.getName(),
                        request.getDate()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Holiday> updateHoliday(
            @PathVariable Long id,
            @Valid
            @RequestBody HolidayRequestDto request) {

        return ResponseEntity.ok(
                holidayService.updateHoliday(
                        id,
                        request.getName(),
                        request.getDate()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoliday(
            @PathVariable Long id) {

        holidayService.deleteHoliday(id);

        return ResponseEntity.noContent().build();
    }
}