package com.ds.app.dto.request;

import java.time.LocalDate;

import com.ds.app.enums.TrainingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingFilterRequestDTO {
	
	private TrainingStatus status;
	private Long        departmentId;
	private LocalDate   fromDate;
	private LocalDate   toDate;
	private int         page = 0;
	private int         size = 10;

}
