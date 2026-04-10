package com.ds.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ds.app.dto.request.EmployeeDocumentRequestDTO;
import com.ds.app.dto.response.EmployeeDocumentResponseDTO;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Documents",description = "Employee document management")
public class EmployeeDocumentController {

	    private static final Logger logger = LoggerFactory.getLogger(EmployeeDocumentController.class);
	
	    @Autowired 
	    EmployeeDocumentService employeeDocumentService;
	    
	    @Autowired
	    EmployeeRepository iEmployeeRepo;
	
	    private Long getUserId(String username) {
	        return iEmployeeRepo.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("Employee not found: " + username))
	                .getUserId();
	    }
	
	    @PostMapping(value = "/finsecure/employee/documents",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation(summary = "Upload document (Employee)",description = 
	    						"Upload: PDF/JPG/PNG, max 5MB.\n" +
	                             "Types: AADHAAR, PAN, PASSPORT, " +
	                             "DRIVING_LICENSE, VOTER_ID, OTHER.\n" +
	                             "One type per employee — cannot upload 2 Aadhaar.")
	    
	    public ResponseEntity<EmployeeDocumentResponseDTO> uploadDocument( @RequestParam("documentType") String documentType,
	    		@RequestParam(value = "documentNumber", required = false)
	            String documentNumber,@RequestParam(value = "expiryDate", required = false) String expiryDate,
	            @RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
	
	        Long userId = getUserId(userDetails.getUsername());
	        EmployeeDocumentRequestDTO dto = new EmployeeDocumentRequestDTO();
	        dto.setDocumentType(documentType);
	        dto.setDocumentNumber(documentNumber);
	        if (expiryDate != null)
	            dto.setExpiryDate(java.time.LocalDate.parse(expiryDate));
	
	        return new ResponseEntity<>(employeeDocumentService.uploadDocument(dto, file, userId), HttpStatus.CREATED);
	    }
	
	    @GetMapping("/finsecure/employee/documents")
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation(summary = "Get my documents (Employee)", description = "Employee views own documents — unmasked.")
	    
	    public ResponseEntity<List<EmployeeDocumentResponseDTO>> getMyDocuments( @AuthenticationPrincipal UserDetails userDetails) {
	       
	    	Long userId = getUserId(userDetails.getUsername());
	        return ResponseEntity.ok( employeeDocumentService.getMyDocuments(userId));
	    }

	    @DeleteMapping("/finsecure/employee/documents/{documentId}")
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation(summary = "Delete document (Employee)", description = "Permanently deletes document file and record.")
	    
	    public ResponseEntity<String> deleteDocument(@PathVariable Integer documentId, @AuthenticationPrincipal UserDetails userDetails) {
	        
	    	Long userId = getUserId(userDetails.getUsername());
	        employeeDocumentService.deleteDocument(documentId, userId);
	        return ResponseEntity.ok( "Document deleted: " + documentId);
	    }
	
	    @GetMapping("/finsecure/hr/employee/{userId}/documents")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(summary = "Get employee documents (HR / Admin)", description = "HR views documents — document number masked.")
	    
	    public ResponseEntity<List<EmployeeDocumentResponseDTO>> getDocumentsByUserId(@PathVariable Long userId) {
	    	
	        return ResponseEntity.ok(employeeDocumentService.getDocumentsByUserId(userId));
	    }
	
	    @PutMapping("/finsecure/hr/documents/{documentId}/verify")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(summary = "Verify document (HR / Admin)", description = "HR marks document as verified " + "after physical check.")
	    
	    public ResponseEntity<EmployeeDocumentResponseDTO> verifyDocument( @PathVariable Integer documentId) {
	    	
	        return ResponseEntity.ok( employeeDocumentService.verifyDocument(documentId));
	    }
	    
}//end class