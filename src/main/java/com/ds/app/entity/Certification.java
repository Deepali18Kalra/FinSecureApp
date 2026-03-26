package com.ds.app.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Certification extends BaseEntity {
	
	
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
	private Long certificationId;
	
	
	@ManyToOne(fetch=jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "employee_id",nullable=false)
	private Employee employee;
	
	
	@ManyToOne(fetch=jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "training_id",nullable=false)
	private Training training;
	
	@Column(nullable=false)
	private String certificationName;
	
	
	@Column(nullable=false)
	private LocalDate issueDate;
	
	
	@Column(nullable=true)
	private String certificateFileUrl;
	
	
	@Column(nullable=false)
	private Boolean verifiedByHr = false;

}
