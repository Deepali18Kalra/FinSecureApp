package com.ds.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends AppUser{
	
	private String firstName;
	private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hr_id")
	@JsonIgnore
	private Employee hr;
	
	@OneToMany(mappedBy = "hr", fetch = FetchType.LAZY)
	private List<Employee> assignedEmployees;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<Attendance> attendanceList;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<Leave> leaves;
	
	@OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
	private List<Leave> approvedLeaves;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<LeaveBalance> leaveBalances;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<Timesheet> timesheets;
	
	@OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
	private List<Timesheet> approvedTimeSheets;
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<RegularizationRequest> regularizationRequests;
	
	@OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
	private List<RegularizationRequest> approvedRegularizations;
}
