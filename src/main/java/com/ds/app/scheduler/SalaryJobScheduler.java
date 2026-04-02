package com.ds.app.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.app.entity.SalaryJob;
import com.ds.app.enums.JobStatus;
import com.ds.app.repository.SalaryJobRepository;
import com.ds.app.service.SalaryService;

import lombok.RequiredArgsConstructor;

@Component
public class SalaryJobScheduler {
	
	@Autowired
	SalaryJobRepository salaryJobRepository;
	@Autowired
    SalaryService salaryService;

    // runs every minute — checks if any scheduled job is due
    @Scheduled(fixedRate = 60000)
    public void checkAndRunDueJobs() {

        List<SalaryJob> dueJobs = salaryJobRepository
                .findByJobStatusAndScheduledDateTimeBefore(
                        JobStatus.SCHEDULED,
                        LocalDateTime.now()
                );

        for (SalaryJob job : dueJobs) {
            System.out.println("Running salary job: " + job.getJobName());
            try {
                salaryService.processSalaryJob(job.getId());
            } catch (Exception e) {
                System.err.println("Job failed: " + job.getJobName()
                        + " — " + e.getMessage());
            }
        }
    }

}
