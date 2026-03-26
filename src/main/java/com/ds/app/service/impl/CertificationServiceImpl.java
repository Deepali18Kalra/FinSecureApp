package com.ds.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ds.app.dto.request.CertificationRequestDTO;
import com.ds.app.dto.response.CertificationResponseDTO;
import com.ds.app.entity.Certification;
import com.ds.app.entity.CertificationStatus;
import com.ds.app.entity.Employee;
import com.ds.app.entity.SkillStatus;
import com.ds.app.entity.Training;
import com.ds.app.repository.CertificationRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.TrainingRepository;
import com.ds.app.service.CertificationService;
import com.ds.app.utils.SecurityUtils;

public class CertificationServiceImpl implements CertificationService{

	
	@Autowired
	private SecurityUtils securityUtils;
	
	@Autowired
	private TrainingRepository trainingRepo;
	
	@Autowired
	private CertificationRepository certRepo;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	
	@Override
	public String uploadCertification(CertificationRequestDTO request) {
		   // get logged in employee
        Employee employee = securityUtils.getLoggedInEmployee();

        Training training = trainingRepo
                .findById(request.getTrainingId())
                .orElseThrow(() -> new RuntimeException(
                        "Training not found"));

        // save certification record
        Certification cert = new Certification();
        cert.setEmployee(employee);
        cert.setTraining(training);
        cert.setCertificationName(request.getCertificationName());
        cert.setIssueDate(request.getIssuedDate());
        cert.setCertificateFileUrl(request.getCertificateFileUrl());
        cert.setVerifiedByHr(false);
        certRepo.save(cert);
        
        // update employee certificationStatus
        employee.setCertificationStatus(
                CertificationStatus.CERTIFIED);
        employeeRepo.save(employee);

        // notify HR — get HR email from securityUtils
        
//        Employee hr = securityUtils.getLoggedInEmployee();
//        emailService.sendCertUploadedEmail(
//                hr.getEmail(),
//                employee.getName(),
//                request.getCertificationName());

        return "Certification uploaded successfully";


	
	}

	@Override
	public String verifyCertification(Long certId) {
		 Certification cert = certRepo.findById(certId)
	                .orElseThrow(() -> new RuntimeException(
	                        "Certification not found"));

	        cert.setVerifiedByHr(true);
	        certRepo.save(cert);

	        // update employee skillStatus too
	        Employee emp = cert.getEmployee();
	        emp.setSkillStatus(SkillStatus.SKILLED);
	        employeeRepo.save(emp);

	        // notify employee
	        
//	        emailService.sendCertVerifiedEmail(
//	                emp.getEmail(),
//	                emp.getName(),
//	                cert.getCertificationName());

	        return "Certification verified successfully";

	
	}

	@Override
	public Page<CertificationResponseDTO> getMyCertifications(int page, int size) {
//		Employee emp = securityUtils.getLoggedInEmployee();
//		
//		  
//	        Pageable pageable = PageRequest.of(page, size);
//
//	        return certRepo.findByEmployee_UserId(
//	                emp.getUserId(), pageable)
//	                .map(this::mapToResponseDTO);

     return null;
	}

	@Override
	public Page<CertificationResponseDTO> getAllCertifications(int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CertificationResponseDTO> getPendingVerifications() {
		
		  List<Certification> certs = certRepo.findByVerifiedByHrFalse();
		  
		  return certs.stream()
				  .map(this::mapToResponseDTO)
				  .toList();
	}
	
	  private CertificationResponseDTO mapToResponseDTO( Certification c) {
	        CertificationResponseDTO dto = new CertificationResponseDTO();
	        dto.setCertificationId(c.getCertificationId());
	        dto.setEmployeeId(c.getEmployee().getUserId());
	        dto.setEmployeeName(c.getEmployee().getFirstName()+" "+c.getEmployee().getLastName());
	        dto.setTrainingId(c.getTraining().getTrainingId());
	        dto.setTrainingName(c.getTraining().getTrainingName());
	        dto.setCertificationName(c.getCertificationName());
	        dto.setIssuedDate(c.getIssueDate());
	        dto.setCertificateFileUrl(c.getCertificateFileUrl());
	        dto.setVerifiedByHr(c.getVerifiedByHr());
	        dto.setUpdatedAt(c.getUpdatedAt());
	        return dto;
	    }


}
