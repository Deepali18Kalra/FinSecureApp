package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name="user_id")
public class Employee extends AppUser{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;
  	private String firstName;
	private String lastName;
	
	
	
	@Column(nullable = false)
	private Double currentSalary;
	
	
	@Column(nullable = false)
	private Boolean isActive = true;
	
	@Column(unique = true)
	private String employeeCode;
	
	private LocalDate joiningDate;
	
	@OneToOne(mappedBy = "employee",cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private EmployeeBankAccount bankAccount;
	
	@OneToMany(mappedBy = "employee" ,cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<EmployeeInvestment>investments;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<EmployeeCard>cards;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<EmployeeCard>salaryRecords;
	

	
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeInsurance> insurances;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeTopUp> topUps;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<InsuranceClaim> insuranceClaims;
    
}