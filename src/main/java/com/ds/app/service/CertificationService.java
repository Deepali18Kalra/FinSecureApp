package com.ds.app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ds.app.dto.request.CertificationRequestDTO;
import com.ds.app.dto.response.CertificationResponseDTO;

public interface CertificationService {
	

    // employee uploads certification
    String uploadCertification(CertificationRequestDTO request);

    // HR verifies certification
    String verifyCertification(Long certId);

    // employee views own certifications
    Page<CertificationResponseDTO> getMyCertifications(
            int page,
            int size);

    // HR/Admin views all certifications
    Page<CertificationResponseDTO> getAllCertifications(
            int page,
            int size);


    // HR views pending verifications
    List<CertificationResponseDTO> getPendingVerifications();

	

}
