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
import com.ds.app.entity.EnrollmentStatus;
import com.ds.app.entity.Training;
import com.ds.app.entity.TrainingStatus;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTrainingRepository;
import com.ds.app.repository.TrainingRepository;
import com.ds.app.service.TrainingService;
import com.ds.app.utils.SecurityUtils;

import jakarta.transaction.Transactional;

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

	
	@Override
	public TrainingResponseDTO createTraining(TrainingRequestDTO request) {
		
		Employee hr = securityUtils.getLoggedInEmployee();
		
		
		if(request.getStartDate().isBefore(LocalDate.now())) {
			throw new RuntimeException("Start date cannot be in the past");
		}
		
		if(request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
			throw new RuntimeException("End date cannot be before start date");
		}

		//Prevent duplicate training
		Optional<Training> existingTraining = trainingRepo.findByTrainingNameAndStartDate(
                request.getTrainingName(),
                request.getStartDate());
		
        if (existingTraining.isPresent()) {
            throw new RuntimeException("Training with the same name already exists in this department");
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
		return mapToResponseDTO(saved);
	}

	@Transactional
	@Override
	public String enrollEmployee(EnrollRequestDTO request) {

        Training training = trainingRepo.findById(
                request.getTrainingId())
                .orElseThrow(() -> new RuntimeException(
                        "Training not found"));
        
        if(training.getStatus()==TrainingStatus.COMPLETED) {
            throw new RuntimeException("Cannot enroll employees in a completed training");
        }
        
        if(request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new RuntimeException("No employees specified for enrollment");
        }

        //Prepare list for batch save
        List<EmployeeTraining> enrollments = new ArrayList<>();
        
        int enrollmentCount = 0;
        int skippedCount = 0;
        
        for (Long empId : request.getEmployeeIds()) {

            Employee emp = employeeRepo.findById(empId.intValue())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found: " + empId));
            

            // check duplicate enrollment
            boolean alreadyEnrolled = empTrainingRepo
                    .existsByEmployee_UserIdAndTraining_TrainingId(
                            emp.getUserId(),
                            training.getTrainingId());
            
            if (alreadyEnrolled) {
            	skippedCount++;
            	continue; // skip already enrolled
            }
            
            
            EmployeeTraining empTraining = new EmployeeTraining();
            empTraining.setEmployee(emp);
            empTraining.setTraining(training);
            empTraining.setStatus(EnrollmentStatus.ENROLLED);
            empTraining.setEnrollmentDate(LocalDate.now());
            empTraining.setEmailSent(false);
            enrollments.add(empTraining);
            enrollmentCount++;
            

            // send enrollment email
//            emailService.sendEnrollmentEmail(
//                    emp.getEmail(),
//                    emp.getName(),
//                    training.getTrainingName());
//
//            et.setEmailSent(true);
//            empTrainingRepo.save(et);
        }
        //Batch save - performance optimized
        if(!enrollments.isEmpty()) {
            empTrainingRepo.saveAll(enrollments);
        }

        return "Employees enrolled successfully: " + enrollmentCount + ", skipped: " + skippedCount;
      }
	

	@Transactional
	@Override
	public String startTraining(Long trainingId) {
		   Training training = trainingRepo.findById(trainingId)
	                .orElseThrow(() -> new RuntimeException(
	                        "Training not found"));
		   
		   if(training.getStatus()==TrainingStatus.IN_PROGRESS) {
               throw new RuntimeException("Training is already in progress");
           }

		   if(training.getStatus()==TrainingStatus.COMPLETED) {
               throw new RuntimeException("Cannot start a completed training");
           }
		   
		   //fetch enrollments
		   List<EmployeeTraining> enrollments = empTrainingRepo.findByTraining_TrainingId(trainingId);
		   
		   if(enrollments.isEmpty()) {
               throw new RuntimeException("No enrollments found for this training");
           }

		   //update training status
	        training.setStatus(TrainingStatus.IN_PROGRESS);
	        trainingRepo.save(training);
            

	        // update all enrollments status + send emails
	          for (EmployeeTraining et : enrollments) {
	            et.setStatus(EnrollmentStatus.IN_PROGRESS);
	           
//
//	            emailService.sendTrainingStartEmail(
//	                    et.getEmployee().getEmail(),
//	                    et.getEmployee().getName(),
//	                    training.getTrainingName(),
//	                    training.getStartDate());
	        }
	          
	          //batch save
	          empTrainingRepo.saveAll(enrollments);
	          
	        return  "Training started successfully "+ enrollments.size() +" employees";

	}

	@Transactional
	@Override
	public String stopTraining(Long trainingId) {
	
		Training training = trainingRepo.findById(trainingId)
				.orElseThrow(() -> new RuntimeException(
                        "Training not found"));
		
		if(training.getStatus() == TrainingStatus.NOT_STARTED) {
			throw new RuntimeException("Cannot stop a training that has not started");
		}
			
		if(training.getStatus() == TrainingStatus.COMPLETED) {
			throw new RuntimeException("Training is already completed");
		}
		
		List<EmployeeTraining>enrollments = empTrainingRepo.findByTraining_TrainingId(trainingId);
		
		if(enrollments.isEmpty()) {
            throw new RuntimeException("No enrollments found for this training");
        }
		
		
	    //update training status
		training.setStatus(TrainingStatus.COMPLETED);
		trainingRepo.save(training);
		
		
		//update all enrollments status + send emails
		for(EmployeeTraining et : enrollments) {
			et.setStatus(EnrollmentStatus.COMPLETED);
			et.setCompletionDate(LocalDate.now());
			

//            emailService.sendTrainingCompleteEmail(
//                    et.getEmployee().getEmail(),
//                    et.getEmployee().getName(),
//                    training.getTrainingName());

		}
		
		//Batch save
		empTrainingRepo.saveAll(enrollments);
		
		return "Training stopped successfully for " + enrollments.size() + " employees";
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
				.orElseThrow(() -> new RuntimeException("Training not found"));
		
		if(Boolean.TRUE.equals(training.isDeleted())) {
			throw new RuntimeException("Training not found");
		}
		
		return mapToResponseDTO(training);
	}

	@Override
	public Page<EmployeeTrainingResponseDTO> getEnrollment(Long trainingId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
		
		Training training = trainingRepo.findById(trainingId)
				.orElseThrow(() -> new RuntimeException("Training not found"));
		
		if(Boolean.TRUE.equals(training.isDeleted())) {
			throw new RuntimeException("Training not availabel");
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
	public Boolean isTrainingCompleted(Long employeeId) {
		  Employee emp = employeeRepo.findByUserId(employeeId)
				  .orElseThrow(()->new RuntimeException("Employee not found"));

	        boolean isCompleted = empTrainingRepo
	        		.existsByEmployee_UserIdAndStatus(employeeId, EnrollmentStatus.COMPLETED);

	        return isCompleted;
	}

	@Override
	public String deleteTraining(Long trainingId) {
		 Training training = trainingRepo.findById(trainingId)
	                .orElseThrow(() -> new RuntimeException(
	                        "Training not found"));

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
