package com.ds.app.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ds.app.enums.TrainingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponseDTO {

	private Long    trainingId;
	private String  trainingName;
	private String  description;
	private LocalDate startDate;
	private LocalDate endDate;
	private TrainingStatus status;
	private Long    createdByHrId;
	private Long    departmentId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Integer  totalEnrolled;
}
