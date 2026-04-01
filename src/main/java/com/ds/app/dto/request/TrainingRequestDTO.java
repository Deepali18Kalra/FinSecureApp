package com.ds.app.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingRequestDTO {
	
	@NotBlank(message = "Training name is required")
	private String trainingName;
	
	
	@Size(max = 500 ,message = "Description too long")
	private String description;
	
	
	@NotNull(message = "Start date is required")
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private Long departmentId;

}
