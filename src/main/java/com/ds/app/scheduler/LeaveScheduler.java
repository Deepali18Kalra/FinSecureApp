package com.ds.app.scheduler;

import com.ds.app.entity.Leave;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.repository.ILeaveRepository;
import com.ds.app.service.ILeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveScheduler {

    private final ILeaveRepository leaveRepo;
    private final ILeaveBalanceService leaveBalanceService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void expireLeaves() {
        List<Leave> listOfPendingLeavesStartDateHasPassed =
                leaveRepo.findByStatusAndStartDateLessThanEqual(LeaveStatus.PENDING, LocalDate.now());

        for (Leave leave : listOfPendingLeavesStartDateHasPassed) {
            // 1) Expire leave
            leave.setStatus(LeaveStatus.EXPIRED);

            // 2) Release reserved balance
            leaveBalanceService.releaseReservedLeaves(
                    leave.getEmployee().getUserId(),
                    leave.getStartDate().getYear(),
                    leave.getLeaveType(),
                    leave.getTotalDays()
            );
        }

        leaveRepo.saveAll(listOfPendingLeavesStartDateHasPassed);
    }
}