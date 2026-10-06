package com.employee360.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.entity.LeaveType;
import com.employee360.repository.LeaveTypeRepository;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceService leaveBalanceService;

    public LeaveTypeService(
            LeaveTypeRepository leaveTypeRepository,
            LeaveBalanceService leaveBalanceService) {

        this.leaveTypeRepository =
                leaveTypeRepository;
        this.leaveBalanceService = leaveBalanceService;
    }

    public List<LeaveType> getAllLeaveTypes() {

        return leaveTypeRepository.findAll();
    }

    public LeaveType getLeaveType(Long id) {

        return leaveTypeRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Leave type not found."
                        )
                );
    }

    @Transactional
    public LeaveType createLeaveType(
            String name,
            int entitlement,
            Integer maxConsecutiveLeave) {

        if (leaveTypeRepository
                .existsByNameIgnoreCase(name)) {

            throw new IllegalArgumentException(
                    "Leave type already exists."
            );
        }

        validateValues(
                entitlement,
                maxConsecutiveLeave
        );

        LeaveType leaveType =
                new LeaveType();

        leaveType.setName(name.trim());
        leaveType.setEntitlement(entitlement);
        leaveType.setMaxConsecutiveLeave(
                maxConsecutiveLeave
        );

        LeaveType savedLeaveType = leaveTypeRepository.save(
                leaveType
        );

        leaveBalanceService.initializeForLeaveType(savedLeaveType);
        return savedLeaveType;
    }

    @Transactional
    public LeaveType updateLeaveType(
            Long id,
            String name,
            int entitlement,
            Integer maxConsecutiveLeave) {

        LeaveType leaveType =
                getLeaveType(id);

        if (!leaveType.getName()
                .equalsIgnoreCase(name.trim())
                && leaveTypeRepository
                        .existsByNameIgnoreCase(
                                name.trim()
                        )) {

            throw new IllegalArgumentException(
                    "Leave type already exists."
            );
        }

        validateValues(
                entitlement,
                maxConsecutiveLeave
        );

        leaveType.setName(name.trim());
        leaveType.setEntitlement(entitlement);
        leaveType.setMaxConsecutiveLeave(
                maxConsecutiveLeave
        );

        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);
        leaveBalanceService.updateEntitlement(savedLeaveType);
        return savedLeaveType;
    }

    public void deleteLeaveType(Long id) {

        LeaveType leaveType =
                getLeaveType(id);

        leaveTypeRepository.delete(leaveType);
    }

    private void validateValues(
            int entitlement,
            Integer maxConsecutiveLeave) {

        if (entitlement < 0) {

            throw new IllegalArgumentException(
                    "Entitlement cannot be negative."
            );
        }

        if (maxConsecutiveLeave != null
                && maxConsecutiveLeave < 1) {

            throw new IllegalArgumentException(
                    "Maximum consecutive leave must be at least 1."
            );
        }
    }
}