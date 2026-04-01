package com.ds.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;

import com.ds.app.enums.JobStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalaryJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;

	private String jobName;

	private YearMonth targetMonth;

	private LocalDateTime scheduledDateTime;

	@Enumerated(EnumType.STRING)
	private JobStatus jobStatus;

	private Integer totalEmployees;

	private Integer successCount;

	private Integer failureCount;

	private Long createdBy;
	
	@CreationTimestamp
	private LocalDateTime createdAt;

}
