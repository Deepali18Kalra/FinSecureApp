package com.ds.app.dto.response;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.SkillStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EligibleEmployeeResponseDTO {
	
	private Long   employeeId;
	private String  name;
	private String  email;
	private EmployeeExperience employeeExperience;
	private CertificationStatus certificationStatus;
	private SkillStatus skillStatus;
	private Long    departmentId;

}
