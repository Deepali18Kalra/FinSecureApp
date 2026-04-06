package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ds.app.dto.request.EnrollRequestDTO;
import com.ds.app.dto.request.TrainingRequestDTO;
import com.ds.app.dto.response.TrainingResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeTraining;
import com.ds.app.entity.Training;
import com.ds.app.enums.EnrollmentStatus;
import com.ds.app.enums.TrainingStatus;
import com.ds.app.exception.CustomException;
import com.ds.app.exception.EmployeeNotFoundException;
import com.ds.app.exception.TrainingNotFoundException;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTrainingRepository;
import com.ds.app.repository.TrainingRepository;
import com.ds.app.utils.SecurityUtils;
@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepo;

    @Mock
    private SecurityUtils securityUtils;
    
    @Mock
    private EmployeeRepository employeeRepo;
    
    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private EmployeeTrainingRepository empTrainingRepo; 

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void testCreateTraining() {
        Employee hr = new Employee();
        hr.setUserId(1L);
        when(securityUtils.getLoggedInEmployee()).thenReturn(hr);

        TrainingRequestDTO request = new TrainingRequestDTO();
        request.setTrainingName("Java Basics");
        request.setDescription("Intro to Java");
        request.setDepartmentId(101L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(5));

        when(trainingRepo.findByTrainingNameAndStartDate(request.getTrainingName(), request.getStartDate()))
                .thenReturn(Optional.empty());

        Training savedTraining = new Training();
        savedTraining.setTrainingId(100L);
        savedTraining.setTrainingName("Java Basics");
        when(trainingRepo.save(any(Training.class))).thenReturn(savedTraining);

       
        when(empTrainingRepo.countByTraining_TrainingId(100L)).thenReturn(0L);

        TrainingResponseDTO response = trainingService.createTraining(request);

        assertNotNull(response);
        assertEquals("Java Basics", response.getTrainingName());
        assertEquals(100L, response.getTrainingId());
    }
    
    
    @Test
    void testEnrollEmployee_TrainingNotFound() {
        EnrollRequestDTO request = new EnrollRequestDTO();
        request.setTrainingId(1L);

        when(trainingRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TrainingNotFoundException.class,
                () -> trainingService.enrollEmployee(request));
    }
    
    @Test
    void testEnrollEmployee_InvalidStatus() {
        Training training = new Training();
        training.setTrainingId(1L);
        training.setStatus(TrainingStatus.IN_PROGRESS);

        EnrollRequestDTO request = new EnrollRequestDTO();
        request.setTrainingId(1L);
        request.setEmployeeIds(Arrays.asList(10L));

        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));

        assertThrows(CustomException.class,
                () -> trainingService.enrollEmployee(request));
    }
    
    @Test
    void testEnrollEmployee_EmployeeNotFound() {
        Training training = new Training();
        training.setTrainingId(1L);
        training.setStatus(TrainingStatus.NOT_STARTED);

        EnrollRequestDTO request = new EnrollRequestDTO();
        request.setTrainingId(1L);
        request.setEmployeeIds(Arrays.asList(10L));

        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(employeeRepo.findById((long) 10)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> trainingService.enrollEmployee(request));
    }
    
    @Test
    void testEnrollEmployee_Success() {
        Training training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Java Basics");
        training.setStatus(TrainingStatus.NOT_STARTED);

        Employee emp = new Employee();
        emp.setUserId(10L);
        emp.setEmail("test@example.com");
        emp.setFirstName("John");
        emp.setLastName("Doe");

        EnrollRequestDTO request = new EnrollRequestDTO();
        request.setTrainingId(1L);
        request.setEmployeeIds(Arrays.asList(10L));

        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(employeeRepo.findById((long) 10)).thenReturn(Optional.of(emp));
        when(empTrainingRepo.existsByEmployee_UserIdAndTraining_TrainingId(10L, 1L))
                .thenReturn(false);

        String result = trainingService.enrollEmployee(request);

        assertTrue(result.contains("Employees enrolled successfully"));
        verify(empTrainingRepo, times(2)).saveAll(anyList()); // once before emails, once after
        verify(emailService).sendEnrollmentEmail(
                eq("test@example.com"),
                eq("John Doe"),
                eq("Java Basics"));
    }
    @Test
    void testStartTraining_Success() {
        
        Training training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Java Basics");
        training.setStatus(TrainingStatus.NOT_STARTED);
        training.setStartDate(LocalDate.of(2026, 4, 10));

        Employee emp = new Employee();
        emp.setUserId(10L);
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setEmail("john@example.com");

        EmployeeTraining et = new EmployeeTraining();
        et.setEmployee(emp);
        et.setTraining(training);
        et.setStatus(EnrollmentStatus.ENROLLED);

        List<EmployeeTraining> enrollments = List.of(et);

        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(empTrainingRepo.findByTraining_TrainingId(1L)).thenReturn(enrollments);

        // Act
        String result = trainingService.startTraining(1L);

        // Assert
        assertEquals("Training started successfully", result);
        assertEquals(TrainingStatus.IN_PROGRESS, training.getStatus());
        assertEquals(EnrollmentStatus.IN_PROGRESS, et.getStatus());
        assertTrue(et.getEmailSent());

        verify(trainingRepo).save(training);
        verify(empTrainingRepo).saveAll(enrollments);
        verify(emailService).sendTrainingStartEmail(
                eq("john@example.com"),
                eq("John Doe"),
                eq("Java Basics"),
                eq("2026-04-10")
        );
    }
}
