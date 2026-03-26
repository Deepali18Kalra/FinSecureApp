package com.ds.app.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificationResponseDTO {
	private Long certificationId;
	private Long employeeId;
	private String employeeName;
	private Long  trainingId;
	private String trainingName;
	private String certificationName;
	private LocalDate issuedDate;
	private String certificateFileUrl;
	private Boolean verifiedByHr;
	private LocalDateTime updatedAt;

}
