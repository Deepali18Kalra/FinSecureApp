package com.ds.app.service;

import org.springframework.data.domain.Page;

import com.ds.app.dto.request.EnrollRequestDTO;
import com.ds.app.dto.request.TrainingRequestDTO;
import com.ds.app.dto.response.EligibleEmployeeResponseDTO;
import com.ds.app.dto.response.EmployeeTrainingResponseDTO;
import com.ds.app.dto.response.TrainingResponseDTO;

public interface TrainingService {
	
	//Hr create Training
	TrainingResponseDTO createTraining(TrainingRequestDTO request);
	
	//Hr enroll employee
	String enrollEmployee(EnrollRequestDTO request);
	
	//Hr start training
	String startTraining(Long trainingId);
	
	//Hr stop training
	String stopTraining(Long trainingId);
	
	//Hr get eligible employees
	Page<EligibleEmployeeResponseDTO> getEligibleEmployees(Long departmentId,int page ,int size);
    
	//Get all training paginated
	Page<TrainingResponseDTO> getAllTrainings(int page,int size);
	
	//Get single Training
	TrainingResponseDTO getTrainingById(Long trainingId);
	
	//Get enrollment of one training
	Page<EmployeeTrainingResponseDTO> getEnrollment(Long trainingId,int page,int size);
	
	// Employee view own Training
	Page<EmployeeTrainingResponseDTO> getMyTraining(int page,int size);
	
	//Hr check if employee complete training
	Boolean isTrainingCompleted(Long employeeId,Long trainingId);
	
	// delete training
	String deleteTraining(Long trainingId);
	
	Boolean isEmployeeCertified(Long employeeId);
	
	
	
	
	
	
	
	
	
}
