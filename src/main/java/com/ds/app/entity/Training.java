package com.ds.app.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ds.app.enums.TrainingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Training  extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long trainingId;
	
	@Column(nullable = false)
	private String trainingName;
	
	@Column(nullable = true)
	private String description;
	
	@Column(nullable = false)
	private LocalDate startDate;
	
	@Column(nullable = true)
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TrainingStatus  status = TrainingStatus.NOT_STARTED;
	
	@Column(nullable = false)
	private Long createdByHrId;
	
	@Column(nullable = true)
	private Long departmentId;
	
	@Column(nullable = false)
	private boolean isDeleted = false;
	
	@OneToMany(mappedBy = "training",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<EmployeeTraining>employeeTrainings = new ArrayList<>();
	
	@OneToMany(mappedBy = "training",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<Certification> certifications = new ArrayList<>();
	
	

}
