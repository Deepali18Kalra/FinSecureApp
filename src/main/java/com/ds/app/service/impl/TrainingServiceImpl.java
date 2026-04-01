package com.ds.app.service.impl;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.EnrollRequestDTO;
import com.ds.app.dto.request.TrainingRequestDTO;
import com.ds.app.dto.response.EligibleEmployeeResponseDTO;
import com.ds.app.dto.response.EmployeeTrainingResponseDTO;
import com.ds.app.dto.response.TrainingResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeTraining;
import com.ds.app.entity.Training;
import com.ds.app.enums.EnrollmentStatus;
import com.ds.app.enums.TrainingStatus;
import com.ds.app.exception.CustomException;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTrainingRepository;
import com.ds.app.repository.TrainingRepository;
import com.ds.app.service.IEmailService;
import com.ds.app.service.TrainingService;
import com.ds.app.utils.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService{
	
	@Autowired
	private TrainingRepository trainingRepo;
	
	@Autowired
	private EmployeeTrainingRepository empTrainingRepo;
	
	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	private SecurityUtils securityUtils;
	
	@Autowired
	private IEmailService emailService;

	
	@Override
	public TrainingResponseDTO createTraining(TrainingRequestDTO request) {
		
		log.info("START:createTraining | Name:{}",request.getTrainingName());
		
		Employee hr = securityUtils.getLoggedInEmployee();
		
		
		if(request.getStartDate().isBefore(LocalDate.now())) {
			log.warn("Invalid start date :{}",request.getStartDate());
			throw new CustomException("Start date cannot be in the past");
		}
		
		if(request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
			log.warn("Invalid end date:{}",request.getEndDate());
			throw new CustomException("End date cannot be before start date");
		}

		//Prevent duplicate training
		Optional<Training> existingTraining = trainingRepo.findByTrainingNameAndStartDate(
                request.getTrainingName(),
                request.getStartDate());
		
        if (existingTraining.isPresent()) {
        	
        	log.warn("Duplicate training found :{}",request.getTrainingName());
            throw new CustomException("Training with the same name already exists in this department");
        }
		
		Training training = new Training();
		training.setTrainingName(request.getTrainingName());
		training.setDescription(request.getDescription());
		training.setDepartmentId(request.getDepartmentId());
		training.setStatus(TrainingStatus.NOT_STARTED);
		training.setCreatedByHrId(hr.getUserId().longValue());
		training.setStartDate(request.getStartDate());
		training.setEndDate(request.getEndDate());
		
		Training saved = trainingRepo.save(training);
		
		log.info("Training created successfully | ID:{}",saved.getTrainingId());
		return mapToResponseDTO(saved);
	}

	@Override
	@Transactional
	public String enrollEmployee(EnrollRequestDTO request) {


	    log.info("START: enrollEmployee | TrainingId: {}", request.getTrainingId());


	    Training training = trainingRepo.findById(request.getTrainingId())
	            .orElseThrow(() -> {
	                log.error("Training not found: {}", request.getTrainingId());
	                return new CustomException("Training not found");
	            });


	    if (training.getStatus() != TrainingStatus.NOT_STARTED) {
	        log.warn("Training not eligible for enrollment: {}", training.getTrainingId());
	        throw new CustomException("Cannot enroll employees");
	    }


	    if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
	        log.warn("No employee IDs provided");
	        throw new CustomException("No employees specified");
	    }


	    List<EmployeeTraining> enrollments = new ArrayList<>();
	    int enrolled = 0, skipped = 0;


	    for (Long empId : request.getEmployeeIds()) {


	        Employee emp = employeeRepo.findById(empId.intValue())
	                .orElseThrow(() -> {
	                    log.error("Employee not found: {}", empId);
	                    return new CustomException("Employee not found");
	                });


	        boolean alreadyEnrolled = empTrainingRepo
	                .existsByEmployee_UserIdAndTraining_TrainingId(
	                        emp.getUserId(),
	                        training.getTrainingId());


	        if (alreadyEnrolled) {
	            skipped++;
	            log.warn("Employee already enrolled: {}", empId);
	            continue;
	        }


	        EmployeeTraining et = new EmployeeTraining();
	        et.setEmployee(emp);
	        et.setTraining(training);
	        et.setStatus(EnrollmentStatus.ENROLLED);
	        et.setEnrollmentDate(LocalDate.now());
	        et.setEmailSent(false);


	        enrollments.add(et);
	        enrolled++;
	    }


	    if (!enrollments.isEmpty()) {
	        empTrainingRepo.saveAll(enrollments);
	    }
	    
	  

	        // Send enrollment emails
	        for (EmployeeTraining et : enrollments) {
	            emailService.sendEnrollmentEmail(
	                et.getEmployee().getEmail(),
	                et.getEmployee().getFirstName() + " " + et.getEmployee().getLastName(),
	                training.getTrainingName()
	            );
	            et.setEmailSent(true);
	        }
	        empTrainingRepo.saveAll(enrollments);
	    


	    log.info("Enrollment completed | Enrolled: {} Skipped: {}", enrolled, skipped);


	    return "Employees enrolled successfully: " + enrolled + ", skipped: " + skipped;
	}




	

	@Override
	@Transactional
	public String startTraining(Long trainingId) {


	    log.info("START: startTraining | TrainingId: {}", trainingId);


	    Training training = trainingRepo.findById(trainingId)
	            .orElseThrow(() -> {
	                log.error("Training not found: {}", trainingId);
	                return new CustomException("Training not found");
	            });


	    if (training.getStatus() == TrainingStatus.IN_PROGRESS) {
	        log.warn("Training already in progress: {}", trainingId);
	        throw new CustomException("Already in progress");
	    }


	    List<EmployeeTraining> enrollments =
	            empTrainingRepo.findByTraining_TrainingId(trainingId);


	    if (enrollments.isEmpty()) {
	        log.warn("No enrollments found for training: {}", trainingId);
	        throw new CustomException("No enrollments found");
	    }


	    training.setStatus(TrainingStatus.IN_PROGRESS);
	    trainingRepo.save(training);


	    enrollments.forEach(et ->{ et.setStatus(EnrollmentStatus.IN_PROGRESS);
	    
	 
	    emailService.sendTrainingStartEmail(
	        et.getEmployee().getEmail(),
	        et.getEmployee().getFirstName() + " " + et.getEmployee().getLastName(),
	        training.getTrainingName(),
	        training.getStartDate().toString()
	    );
	    et.setEmailSent(true);
	});
	    empTrainingRepo.saveAll(enrollments);


	    log.info("Training started | Employees affected: {}", enrollments.size());


	    return "Training started successfully";
	}



	@Override
	@Transactional
	public String stopTraining(Long trainingId) {


	    log.info("START: stopTraining | TrainingId: {}", trainingId);


	    Training training = trainingRepo.findById(trainingId)
	            .orElseThrow(() -> {
	                log.error("Training not found: {}", trainingId);
	                return new CustomException("Training not found");
	            });


	    if (training.getStatus() == TrainingStatus.NOT_STARTED) {
	        log.warn("Cannot stop not-started training: {}", trainingId);
	        throw new CustomException("Cannot stop");
	    }


	    List<EmployeeTraining> enrollments =
	            empTrainingRepo.findByTraining_TrainingId(trainingId);


	    training.setStatus(TrainingStatus.COMPLETED);
	    trainingRepo.save(training);


	    enrollments.forEach(et -> {
	        et.setStatus(EnrollmentStatus.COMPLETED);
	        et.setCompletionDate(LocalDate.now());
	        emailService.sendTrainingCompleteEmail(
	                et.getEmployee().getEmail(),
	                et.getEmployee().getFirstName() + " " + et.getEmployee().getLastName(),
	                training.getTrainingName()
	            );
	            et.setEmailSent(true);
	    });


	    empTrainingRepo.saveAll(enrollments);


	    log.info("Training stopped | Employees affected: {}", enrollments.size());


	    return "Training stopped successfully";
	}





	@Override
	public Page<EligibleEmployeeResponseDTO> getEligibleEmployees(Long departmentId, int page, int size) {
		


		    Pageable pageable = PageRequest.of(page, size,Sort.by("userId"));

		    Page<Employee> employees;

		    if (departmentId != null) {
		        employees = employeeRepo.findEligibleByDepartment(departmentId, pageable);
		    } else {
		        employees = employeeRepo.findEligibleForTraining(pageable);
		    }

		    return employees.map(this::mapToEligibleDTO);
		
		   
		}
	

	@Override
	public Page<TrainingResponseDTO> getAllTrainings(int page, int size) {
		 Pageable pageable = PageRequest.of(page, size,
	                Sort.by("createdAt").descending());

	        return trainingRepo.findByIsDeletedFalse(pageable)
	                .map(this::mapToResponseDTO);
	}

	@Override
	public TrainingResponseDTO getTrainingById(Long trainingId) {
		Training training = trainingRepo.findById(trainingId)
				.orElseThrow(() -> new CustomException("Training not found"));
		
		if(Boolean.TRUE.equals(training.isDeleted())) {
			throw new CustomException("Training not found");
		}
		
		return mapToResponseDTO(training);
	}

	@Override
	public Page<EmployeeTrainingResponseDTO> getEnrollment(Long trainingId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
		
		Training training = trainingRepo.findById(trainingId)
				.orElseThrow(() -> new CustomException("Training not found"));
		
		if(Boolean.TRUE.equals(training.isDeleted())) {
			throw new CustomException("Training not availabel");
		} 

		Page<EmployeeTraining> enrollments = empTrainingRepo.findByTraining_TrainingId(trainingId, pageable);
		
		return enrollments.map(this::mapToEnrollmentDTO);
	}

	@Override
	public Page<EmployeeTrainingResponseDTO> getMyTraining(int page, int size) {

        Employee emp = securityUtils.getLoggedInEmployee();
        Pageable pageable = PageRequest.of(page, size,Sort.by("createdAt").descending());

        Page<EmployeeTraining> enrollments = empTrainingRepo.findByEmployee_UserId(emp.getUserId(), pageable);
        return enrollments.map(this::mapToEnrollmentDTO);
		
	}

	@Override
	public Boolean isTrainingCompleted(Long employeeId,Long trainingId) {
		  Employee emp = employeeRepo.findByUserId(employeeId)
				  .orElseThrow(()->new CustomException("Employee not found"));

	  Training training = trainingRepo.findById(trainingId)
			  .orElseThrow(() -> new CustomException("Training not found"));

	  boolean isCompleted = empTrainingRepo
			  .existsByEmployee_UserIdAndTraining_TrainingIdAndStatus(emp.getUserId(), training.getTrainingId(), EnrollmentStatus.COMPLETED);

	        return isCompleted; 
	}

	@Transactional
	@Override
	public String deleteTraining(Long trainingId) {
		 Training training = trainingRepo.findById(trainingId)
	                .orElseThrow(() -> new CustomException(
	                        "Training not found"));

		 if(Boolean.TRUE.equals(training.isDeleted())) {
	            throw new CustomException("Training not found");
	        }
		 if(training.getStatus() == TrainingStatus.IN_PROGRESS) {
	            throw new CustomException("Cannot delete training in progress");
	        }
		 
	        training.setDeleted(true);
	        trainingRepo.save(training);
	        return "Training deleted successfully";

	}
	
	
	  // ─── PRIVATE MAPPERS ───────────────────────────────
    private TrainingResponseDTO mapToResponseDTO(Training t) {
        TrainingResponseDTO dto = new TrainingResponseDTO();
        dto.setTrainingId(t.getTrainingId());
        dto.setTrainingName(t.getTrainingName());
        dto.setDescription(t.getDescription());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setStatus(t.getStatus());
        dto.setCreatedByHrId(t.getCreatedByHrId());
        dto.setDepartmentId(t.getDepartmentId());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        dto.setTotalEnrolled(
                empTrainingRepo.countByTraining_TrainingId(
                        t.getTrainingId()).intValue());
        return dto;
    }
    
    private EmployeeTrainingResponseDTO mapToEnrollmentDTO(
            EmployeeTraining et) {
    EmployeeTrainingResponseDTO dto =
            new EmployeeTrainingResponseDTO();
    dto.setId(et.getId());
    dto.setEmployeeId(et.getEmployee().getUserId().longValue());
    dto.setEmployeeName(et.getEmployee().getFirstName() + " " + et.getEmployee().getLastName());
    dto.setEmployeeEmail(et.getEmployee().getEmail());
    dto.setTrainingId(et.getTraining().getTrainingId());
    dto.setTrainingName(et.getTraining().getTrainingName());
    dto.setStatus(et.getStatus());
    dto.setEnrolledDate(et.getEnrollmentDate());
    dto.setCompletionDate(et.getCompletionDate());
    dto.setEmailSent(et.getEmailSent());
    dto.setCreatedAt(et.getCreatedAt());
    return dto;
}
    
    private EligibleEmployeeResponseDTO mapToEligibleDTO(Employee e) {
        EligibleEmployeeResponseDTO dto = new EligibleEmployeeResponseDTO();
        dto.setEmployeeId(e.getUserId());
        dto.setName(e.getFirstName()+" "+e.getLastName());
        dto.setEmail(e.getEmail());
        dto.setEmployeeExperience(e.getEmployeeExperience());
        dto.setCertificationStatus(e.getCertificationStatus());
        dto.setSkillStatus(e.getSkillStatus());
        dto.setDepartmentId(e.getDepartmentId());
        return dto;
    }





}
