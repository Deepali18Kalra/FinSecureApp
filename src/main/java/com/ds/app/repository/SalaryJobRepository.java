package com.ds.app.repository;

import com.ds.app.entity.SalaryJob;
import com.ds.app.enums.JobStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryJobRepository extends JpaRepository<SalaryJob, Long> {

	Page<SalaryJob> findAll(Pageable pageable);

	// scheduler uses this to find jobs ready to run
	List<SalaryJob> findByJobStatusAndScheduledDateTimeBefore(JobStatus status, LocalDateTime dateTime);
}
