package com.ds.app.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeIncompleteResponseDTO {

    private Long userId;
    private String employeeCode;
    private String fullName;  // firstName + lastName
    private List<String> missingFields;
    private Boolean hasEducation;    // true = added
    private Boolean hasDocuments;    // true = uploaded
    
}