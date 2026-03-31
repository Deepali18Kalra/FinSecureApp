package com.ds.app.scheduler;

import com.ds.app.entity.Employee;
import com.ds.app.entity.LeaveBalance;
import com.ds.app.repository.IEmployeeRepository;
import com.ds.app.repository.ILeaveBalanceRepository;
import com.ds.app.repository.ILeaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveBalanceScheduler {

    private final IEmployeeRepository employeeRepository;
    private final ILeaveBalanceRepository leaveBalanceRepository;

    @Scheduled(cron = "0 1 0 1 1 *")
    @Transactional
    public void createYearlyLeaveBalances() {
        int year = Year.now().getValue();

        List<Employee> allEmployees = employeeRepository.findAll();

        allEmployees.stream()
                .filter(emp -> !leaveBalanceRepository.existsByEmployeeUserIdAndYear(emp.getUserId(), year))
                .forEach(emp -> {
                    BigDecimal carryForward = leaveBalanceRepository
                            .findByEmployeeUserIdAndYear(emp.getUserId(), year - 1)
                            .map(prev -> prev.getEarnedLeaveBalance() == null
                                    ? BigDecimal.ZERO
                                    : prev.getEarnedLeaveBalance().max(BigDecimal.ZERO))
                            .orElse(BigDecimal.ZERO);
                    LeaveBalance newBalance = LeaveBalance.builder()
                            .employee(emp)
                            .year(year)
                            .sickLeaveBalance(10)
                            .casualLeaveBalance(8)
                            .earnedLeaveBalance(carryForward)
                            .carriedForwardEarnedDays(carryForward)
                            .build();

                    leaveBalanceRepository.save(newBalance);
                });
    }
}
