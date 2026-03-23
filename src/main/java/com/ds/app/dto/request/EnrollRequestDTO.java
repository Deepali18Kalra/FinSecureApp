package com.ds.app.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollRequestDTO {

	@NotNull(message = "Training ID cannot be null")
	private Long trainingId;
	
	@NotEmpty(message = "At least one employee ID must be provided")
	private List<Long> employeeIds;
}
