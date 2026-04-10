package com.ds.app.dto.response;
 
import java.util.List;

import com.ds.app.dto.response.EmployeeDocumentResponseDTO;
import com.ds.app.dto.response.EmployeeEducationResponseDTO;
import com.ds.app.dto.response.EmployeeProfileResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeExportDTO {
 
    // ── Complete employee profile ──────────────────────────────────
    private EmployeeProfileResponseDTO profile;
 
  
    // Empty list if no education records added yet
    private List<EmployeeEducationResponseDTO> education;
 

    // Document numbers masked — HR view
    // Empty list if no documents uploaded yet
    private List<EmployeeDocumentResponseDTO> documents;
    
}