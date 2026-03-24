package com.ds.app.dto.response;

import com.ds.app.entity.EmployeeExperience;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EligibleEmployeeResponseDTO {
	
	private Long    employeeId;
	private String  name;
	private String  email;
	private EmployeeExperience employeeExperience;
	private Boolean isCertified;
	private Long    departmentId;

}
