package com.ds.app.entity;

import java.time.LocalDate;

import com.ds.app.enums.EnrollmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTraining extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	
	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "training_id", nullable = false)
	private Training training;
	
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EnrollmentStatus status = EnrollmentStatus.ENROLLED;
	
	
	@Column(nullable = false)
	private LocalDate enrollmentDate;
	
	
	@Column(nullable = true)
	private LocalDate completionDate;
	
	
	@Column(nullable = false)
	private Boolean emailSent = false;

}
