package com.employee360.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.employee360.entity.Holiday;
import com.employee360.entity.LeaveBalance;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.LeaveType;
import com.employee360.entity.User;
import com.employee360.repository.HolidayRepository;
import com.employee360.repository.LeaveBalanceRepository;
import com.employee360.repository.LeaveRequestRepository;

@Service
public class PolicyService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final HolidayRepository holidayRepository;

    public PolicyService(
            LeaveBalanceRepository leaveBalanceRepository,
            LeaveRequestRepository leaveRequestRepository,
            HolidayRepository holidayRepository) {

        this.leaveBalanceRepository =
                leaveBalanceRepository;

        this.leaveRequestRepository =
                leaveRequestRepository;

        this.holidayRepository =
                holidayRepository;
    }

    public List<String> validateLeaveRequest(
            User user,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate) {

        List<String> errors =
                new ArrayList<>();

        if (leaveType == null) {

            errors.add(
                    "Leave type is required."
            );

            return errors;
        }

        if (startDate == null
                || endDate == null) {

            errors.add(
                    "Start date and end date are required."
            );

            return errors;
        }

        if (startDate.isAfter(endDate)) {

            errors.add(
                    "Start date cannot be after end date."
            );

            return errors;
        }

        long workingDays =
                calculateWorkingDays(
                        startDate,
                        endDate
                );

        if (workingDays <= 0) {

            errors.add(
                    "Leave must contain at least one working day."
            );
        }

        Integer maxConsecutiveLeave =
                leaveType.getMaxConsecutiveLeave();

        if (maxConsecutiveLeave != null
                && workingDays > maxConsecutiveLeave) {

            errors.add(
                    "Leave exceeds the maximum consecutive leave limit of "
                            + maxConsecutiveLeave
                            + " working days."
            );
        }

        LeaveBalance leaveBalance =
                leaveBalanceRepository
                        .findByUserAndLeaveType(
                                user,
                                leaveType
                        );

        if (leaveBalance == null) {

            errors.add(
                    "Leave balance is not configured for this leave type."
            );

        } else if (leaveBalance.getRemainingLeaves()
                < workingDays) {

            errors.add(
                    "Insufficient remaining leave balance."
            );
        }

        List<LeaveRequest> overlappingRequests =
                leaveRequestRepository
                        .findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                user,
                                endDate,
                                startDate
                        );

        for (LeaveRequest request :
                overlappingRequests) {

            if ("PENDING".equals(request.getStatus())
                    || "APPROVED".equals(
                            request.getStatus())) {

                errors.add(
                        "Leave dates overlap with an existing "
                                + request.getStatus().toLowerCase()
                                + " leave request."
                );

                break;
            }
        }

        return errors;
    }

    public long calculateWorkingDays(
            LocalDate startDate,
            LocalDate endDate) {

        long workingDays = 0;

        LocalDate currentDate =
                startDate;

        while (!currentDate.isAfter(endDate)) {

            DayOfWeek day =
                    currentDate.getDayOfWeek();

            boolean weekend =
                    day == DayOfWeek.SATURDAY
                            || day == DayOfWeek.SUNDAY;

            Holiday holiday =
                    holidayRepository.findByDate(
                            currentDate
                    );

            boolean holidayDate =
                    holiday != null;

            if (!weekend && !holidayDate) {
                workingDays++;
            }

            currentDate =
                    currentDate.plusDays(1);
        }

        return workingDays;
    }
}