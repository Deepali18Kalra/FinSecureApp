package com.ds.app.entity;

import java.time.LocalDate;
import java.util.List;

import com.ds.app.enums.Status;
import com.ds.app.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Employee extends AppUser{
	
	
	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	
	@Column(nullable = false)
	private Double currentSalary;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;
	
	@Column(nullable = false)
	private String email;
	
	@Column(unique = true)
	private String employeeCode;
	
	private LocalDate joiningDate;
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
	
	@OneToOne(mappedBy = "employee",cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private EmployeeBankAccount bankAccount;
	
	@OneToMany(mappedBy = "employee" ,cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<EmployeeInvestment>investments;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<EmployeeCard>cards;
	
	@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<SalaryRecord>salaryRecords;
	
	
	public Long getCompanyId()    { return company    != null ? company.getId()    : null; }


	public Employee(String username, String password, Boolean isAccountLocked, UserRole role, String firstName,
			String lastName, String employeeCode) {
		
		super(username, password, isAccountLocked, role);
		this.firstName = firstName;
		this.lastName = lastName;
		this.employeeCode = employeeCode;
	}


   // changes made by Ashish 
	
	


}


	

	
	
	
	
	
	

	

