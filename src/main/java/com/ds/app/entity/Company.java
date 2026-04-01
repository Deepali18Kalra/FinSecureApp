package com.ds.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Data
@AllArgsConstructor

@NoArgsConstructor

@Entity
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String code; // unique e.g. ICICI-001
	private Boolean restrictsInvestment = false;
	private String status = "ACTIVE"; // ACTIVE / INACTIVE

//    // bidirectional — @JsonIgnore prevents loop when serialising Company
//    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Department> departments = new ArrayList<>();
// 
//    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Project> projects = new ArrayList<>();
// 
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Employee> employees = new ArrayList<>();

}
