package com.ds.app.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ds.app.enums.EnrollmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTrainingResponseDTO {
	
	private Long   id;
	private Long   employeeId;
	private String employeeName;
	private String employeeEmail;
	private Long   trainingId;
	private String trainingName;
	private EnrollmentStatus status;
	private LocalDate  enrolledDate;
	private LocalDate completionDate;
	private Boolean  emailSent;
	private LocalDateTime createdAt;

}
