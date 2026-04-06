package com.ds.app.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.dto.request.CertificationRequestDTO;
import com.ds.app.dto.response.CertificationResponseDTO;
import com.ds.app.exception.ApiResponse;
import com.ds.app.service.CertificationService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/finsecure/certification")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    // UPLOAD CERTIFICATION
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadCertification(
            @Valid @RequestBody CertificationRequestDTO request) {

        log.info("START: uploadCertification | TrainingId: {}", request.getTrainingId());
        String msg = certificationService.uploadCertification(request);
        log.info("END: uploadCertification | Result: {}", msg);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        msg, null)
        );
    }

    // VERIFY CERTIFICATION
    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/verify/{certId}")
    public ResponseEntity<ApiResponse<String>> verifyCertification(
            @PathVariable Long certId) {

        log.info("START: verifyCertification | CertId: {}", certId);
        String msg = certificationService.verifyCertification(certId);
        log.info("END: verifyCertification | Result: {}", msg);

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        msg, null)
        );
    }

    // GET MY CERTIFICATIONS
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<CertificationResponseDTO>>> getMyCertifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("START: getMyCertifications | Page: {} Size: {}", page, size);
        Page<CertificationResponseDTO> data =
                certificationService.getMyCertifications(page, size);
        log.info("END: getMyCertifications | Count: {}", data.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "My certifications fetched", data)
        );
    }

    // GET ALL CERTIFICATIONS (HR)
    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<CertificationResponseDTO>>> getAllCertifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("START: getAllCertifications | Page: {} Size: {}", page, size);
        Page<CertificationResponseDTO> data =
                certificationService.getAllCertifications(page, size);
        log.info("END: getAllCertifications | Count: {}", data.getTotalElements());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "All certifications fetched", data)
        );
    }

    // GET PENDING VERIFICATIONS
    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<CertificationResponseDTO>>> getPendingVerifications() {

        log.info("START: getPendingVerifications");
        List<CertificationResponseDTO> data =
                certificationService.getPendingVerifications();
        log.info("END: getPendingVerifications | Count: {}", data.size());

        return ResponseEntity.ok(
                new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                        "Pending certifications fetched", data)
        );
    }
    
    
}
