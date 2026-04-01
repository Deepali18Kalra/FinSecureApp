package com.ds.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.CertificationRequestDTO;
import com.ds.app.dto.response.CertificationResponseDTO;
import com.ds.app.entity.Certification;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Training;
import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EnrollmentStatus;
import com.ds.app.enums.SkillStatus;
import com.ds.app.exception.CustomException;
import com.ds.app.repository.CertificationRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTrainingRepository;
import com.ds.app.repository.TrainingRepository;
import com.ds.app.service.CertificationService;
import com.ds.app.service.IEmailService;
import com.ds.app.utils.SecurityUtils;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CertificationServiceImpl implements CertificationService {


    @Autowired
    private SecurityUtils securityUtils;


    @Autowired
    private TrainingRepository trainingRepo;


    @Autowired
    private CertificationRepository certRepo;


    @Autowired
    private EmployeeRepository employeeRepo;


    @Autowired
    private EmployeeTrainingRepository empTrainingRepo;
    
    @Autowired
    private IEmailService emailService;



    @Override
    public String uploadCertification(CertificationRequestDTO request) {


        log.info("START: uploadCertification | TrainingId: {}", request.getTrainingId());


        // get logged in employee
        Employee employee = securityUtils.getLoggedInEmployee();
        if (employee == null) {
            log.error("Logged in employee not found");
            throw new CustomException("Logged in employee not found");
        }


        log.info("Employee found: {}", employee.getUserId());


        // get training
        Training training = trainingRepo.findById(request.getTrainingId())
                .orElseThrow(() -> {
                    log.error("Training not found with id: {}", request.getTrainingId());
                    return new CustomException("Training not found");
                });


        log.info("Training found: {}", training.getTrainingId());


        // check completion
        boolean isCompleted = empTrainingRepo
                .existsByEmployee_UserIdAndTraining_TrainingIdAndStatus(
                        employee.getUserId(),
                        training.getTrainingId(),
                        EnrollmentStatus.COMPLETED);


        if (!isCompleted) {
            log.warn("Training not completed for employeeId: {}", employee.getUserId());
            throw new CustomException("Training not completed");
        }


        // duplicate check
        boolean alreadyExists = certRepo
                .existsByEmployee_UserIdAndTraining_TrainingId(
                        employee.getUserId(),
                        training.getTrainingId());


        if (alreadyExists) {
            log.warn("Duplicate certification attempt for employeeId: {}", employee.getUserId());
            throw new CustomException("Certification already exists");
        }


        // save certification
        Certification cert = new Certification();
        cert.setEmployee(employee);
        cert.setTraining(training);
        cert.setCertificationName(request.getCertificationName());
        cert.setIssueDate(request.getIssuedDate());
        cert.setCertificateFileUrl(request.getCertificateFileUrl());
        cert.setVerifiedByHr(false);


        certRepo.save(cert);
        log.info("Certification saved with id: {}", cert.getCertificationId());


        // update employee status
        employee.setCertificationStatus(CertificationStatus.CERTIFIED);
        employeeRepo.save(employee);
        

     // send email to HR
     String hrEmail = "hr@finsecure.com"; // or fetch dynamically from HR records
     emailService.sendCertUploadEmail(
         hrEmail,
         employee.getFirstName() + " " + employee.getLastName(),
         cert.getCertificationName()
     );

     log.info("Certification upload email sent to HR");


        log.info("Employee certification status updated for employeeId: {}", employee.getUserId());


        log.info("END: uploadCertification SUCCESS");


        return "Certification uploaded successfully";
    }


    @Override
    public String verifyCertification(Long certId) {


        log.info("START: verifyCertification | certId: {}", certId);


        if (certId == null || certId <= 0) {
            log.error("Invalid certification ID: {}", certId);
            throw new CustomException("Invalid certification ID");
        }


        Certification cert = certRepo.findById(certId)
                .orElseThrow(() -> {
                    log.error("Certification not found: {}", certId);
                    return new CustomException("Certification not found");
                });


        if (Boolean.TRUE.equals(cert.getVerifiedByHr())) {
            log.warn("Certification already verified: {}", certId);
            throw new CustomException("Certification already verified");
        }


        cert.setVerifiedByHr(true);
        certRepo.save(cert);
        

Employee emp = cert.getEmployee();
emp.setSkillStatus(SkillStatus.SKILLED);
employeeRepo.save(emp);

// send verification email to employee
emailService.sendCertVerificationEmail(
    emp.getEmail(),
    emp.getFirstName() + " " + emp.getLastName(),
    cert.getCertificationName()
);

log.info("Certification verification email sent to employee");


        log.info("Certification verified: {}", certId);


        Employee emp1 = cert.getEmployee();
        emp1.setSkillStatus(SkillStatus.SKILLED);
        employeeRepo.save(emp1);


        log.info("Employee skill updated: {}", emp1.getUserId());


        log.info("END: verifyCertification SUCCESS");


        return "Certification verified successfully";
    }


    @Override
    public Page<CertificationResponseDTO> getMyCertifications(int page, int size) {


        log.info("Fetching certifications for logged-in employee");


        Employee emp = securityUtils.getLoggedInEmployee();
        if (emp == null) {
            log.error("Logged in employee not found");
            throw new CustomException("Logged in employee not found");
        }


        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());


        Page<Certification> certs = certRepo.findByEmployee_UserId(emp.getUserId(), pageable);


        if (certs.isEmpty()) {
            log.warn("No certifications found for employeeId: {}", emp.getUserId());
            return Page.empty();
        }


        log.info("Certifications fetched count: {}", certs.getTotalElements());


        return certs.map(this::mapToResponseDTO);
    }


    @Override
    public Page<CertificationResponseDTO> getAllCertifications(int page, int size) {


        log.info("Fetching all certifications");


        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Certification> certs = certRepo.findAll(pageable);


        if (certs.isEmpty()) {
            log.warn("No certifications found in system");
            return Page.empty();
        }


        log.info("Total certifications: {}", certs.getTotalElements());


        return certs.map(this::mapToResponseDTO);
    }


    @Override
    public List<CertificationResponseDTO> getPendingVerifications() {


        log.info("Fetching pending certifications");


        List<Certification> certs = certRepo.findByVerifiedByHrFalse();


        if (certs.isEmpty()) {
            log.warn("No pending certifications found");
            return Collections.emptyList();
        }


        log.info("Pending certifications count: {}", certs.size());


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
