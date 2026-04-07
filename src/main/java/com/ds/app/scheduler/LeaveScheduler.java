package com.ds.app.scheduler;

import com.ds.app.entity.Leave;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.repository.ILeaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveScheduler {

    private final ILeaveRepository leaveRepo;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void expireLeaves() {
        List<Leave> listOfPendingLeavesStartDateHasPassed = leaveRepo.findByStatusAndStartDateBefore(LeaveStatus.PENDING, LocalDate.now());
        listOfPendingLeavesStartDateHasPassed
                .forEach(leave -> leave.setStatus(LeaveStatus.EXPIRED));

        leaveRepo.saveAll(listOfPendingLeavesStartDateHasPassed);
    }
}
