package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.ds.app.dto.request.CertificationRequestDTO;
import com.ds.app.entity.Certification;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Training;
import com.ds.app.enums.EnrollmentStatus;
import com.ds.app.exception.EmployeeNotFoundException;
import com.ds.app.repository.CertificationRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTrainingRepository;
import com.ds.app.repository.TrainingRepository;
import com.ds.app.service.IEmailService;
import com.ds.app.utils.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class CertificationServiceImplTest {

    @Mock 
    private SecurityUtils securityUtils;
    @Mock 
    private TrainingRepository trainingRepo;
    @Mock
    private CertificationRepository certRepo;
    @Mock
    private EmployeeRepository employeeRepo;
    @Mock 
    private EmployeeTrainingRepository empTrainingRepo;
    @Mock
    private IEmailService emailService;

    @InjectMocks
    private CertificationServiceImpl certificationService;

    @Test
    void testUploadCertification_EmployeeNotFound() {
        CertificationRequestDTO request = new CertificationRequestDTO();
        request.setTrainingId(1L);

        when(securityUtils.getLoggedInEmployee()).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class,
                () -> certificationService.uploadCertification(request));
    }
    
    @Test
    void testUploadCertification_Success() {
        // Arrange
        Employee emp = new Employee();
        emp.setUserId(10L);
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setEmail("john@example.com");

        Training training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Java Basics");
        training.setCreatedByHrId(99L);

        Employee hr = new Employee();
        hr.setUserId(99L);
        hr.setEmail("hr@example.com");

        CertificationRequestDTO request = new CertificationRequestDTO();
        request.setTrainingId(1L);
        request.setCertificationName("Java Cert");
        request.setIssuedDate(LocalDate.now());
        request.setCertificateFileUrl("http://file");

        when(securityUtils.getLoggedInEmployee()).thenReturn(emp);
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(empTrainingRepo.existsByEmployee_UserIdAndTraining_TrainingIdAndStatus(10L, 1L, EnrollmentStatus.COMPLETED))
                .thenReturn(true);
        when(certRepo.existsByEmployee_UserIdAndTraining_TrainingId(10L, 1L)).thenReturn(false);
        when(employeeRepo.findByUserId(99L)).thenReturn(Optional.of(hr));

        // Act
        String result = certificationService.uploadCertification(request);

        // Assert
        assertEquals("Certification uploaded successfully", result);
        verify(certRepo).save(any(Certification.class));
        verify(employeeRepo).save(emp);
        verify(emailService).sendCertUploadEmail(
                eq("hr@example.com"),
                eq("John Doe"),
                eq("Java Cert"));
    }

  
}
