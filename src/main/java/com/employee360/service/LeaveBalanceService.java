package com.employee360.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.entity.LeaveBalance;
import com.employee360.entity.LeaveType;
import com.employee360.entity.User;
import com.employee360.repository.LeaveBalanceRepository;
import com.employee360.repository.LeaveTypeRepository;
import com.employee360.repository.UserRepository;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;

    public LeaveBalanceService(
            LeaveBalanceRepository leaveBalanceRepository,
            LeaveTypeRepository leaveTypeRepository,
            UserRepository userRepository) {

        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void initializeMissingBalances() {
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (User user : userRepository.findAll()) {
            createMissingBalances(user, leaveTypes);
        }
    }

    @Transactional
    public void initializeForUser(User user) {
        createMissingBalances(user, leaveTypeRepository.findAll());
    }

    @Transactional
    public void initializeForLeaveType(LeaveType leaveType) {
        for (User user : userRepository.findAll()) {
            createMissingBalance(user, leaveType);
        }
    }

    @Transactional
    public void updateEntitlement(LeaveType leaveType) {
        for (User user : userRepository.findAll()) {
            LeaveBalance balance =
                    leaveBalanceRepository.findByUserAndLeaveType(user, leaveType);

            if (balance == null) {
                createMissingBalance(user, leaveType);
                continue;
            }

            balance.setTotalLeaves(leaveType.getEntitlement());
            balance.setRemainingLeaves(
                    Math.max(0, leaveType.getEntitlement() - balance.getUsedLeaves()));
            leaveBalanceRepository.save(balance);
        }
    }

    private void createMissingBalances(User user, List<LeaveType> leaveTypes) {
        for (LeaveType leaveType : leaveTypes) {
            createMissingBalance(user, leaveType);
        }
    }

    private void createMissingBalance(User user, LeaveType leaveType) {
        if (leaveBalanceRepository.findByUserAndLeaveType(user, leaveType) != null) {
            return;
        }

        LeaveBalance balance = new LeaveBalance();
        balance.setUser(user);
        balance.setLeaveType(leaveType);
        balance.setTotalLeaves(leaveType.getEntitlement());
        balance.setUsedLeaves(0);
        balance.setRemainingLeaves(leaveType.getEntitlement());
        leaveBalanceRepository.save(balance);
    }
}
