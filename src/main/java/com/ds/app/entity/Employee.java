package com.ds.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.SkillStatus;

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
public class Employee extends AppUser{
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private EmployeeExperience employeeExperience;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private CertificationStatus certificationStatus;
	
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private SkillStatus  skillStatus;
	
	@Column
	private Long departmentId;
	
	//---------------------------changes made by saurabh-----------------------
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<EmployeeTraining> employeeTrainings= new ArrayList<>();
	
	@OneToMany(mappedBy ="employee",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private List<Certification> certifications = new ArrayList<>();
	

	
}
