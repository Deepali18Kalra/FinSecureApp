package com.ds.app.dto.request;

import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFilterRequestDTO {
	
	 	private String firstName;
	    private String designation;
	    private EmploymentType employmentType;
	    private EmployeeExperience employeeExperience;
	    private Boolean isDeleted;
	    private Boolean isAccountLocked;

}
