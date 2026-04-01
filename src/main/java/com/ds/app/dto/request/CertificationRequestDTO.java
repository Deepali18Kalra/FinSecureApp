package com.ds.app.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificationRequestDTO {

	@NotNull(message= "Training ID is required")
	private Long trainingId;
	
	@NotBlank(message= "Certification name is required")
	private String certificationName;
	
	@NotNull(message = "Issued date is required")
	private LocalDate issuedDate;
	
	@NotBlank(message = "Certification file URL is required")
	private String certificateFileUrl;
}
