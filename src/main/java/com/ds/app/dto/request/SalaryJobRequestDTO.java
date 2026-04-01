package com.ds.app.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryJobRequestDTO {

	@NotBlank(message = "Job name is required")
    private String jobName;

    @NotNull(message = "Scheduled date-time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledDateTime;

    @NotBlank(message = "Target month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
             message = "Format must be YYYY-MM e.g. 2025-03")
    
    private String targetMonth;
}
