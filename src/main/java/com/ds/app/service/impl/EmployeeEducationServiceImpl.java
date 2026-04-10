package com.ds.app.service.impl;
 
import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ds.app.dto.request.EmployeeEducationRequestDTO;
import com.ds.app.dto.response.EmployeeEducationResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeEducation;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.repository.EmployeeEducationRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeEducationService;
 
@Service
@Transactional
public class EmployeeEducationServiceImpl implements EmployeeEducationService {
 
    private static final Logger logger = LoggerFactory.getLogger(EmployeeEducationServiceImpl.class);
 
		
		@Autowired
		EmployeeEducationRepository educationRepo;
		@Autowired
		EmployeeRepository iEmployeeRepo;
 
  

    @Override
    public EmployeeEducationResponseDTO addEducation( EmployeeEducationRequestDTO dto, Long userId) throws Exception {
 
        logger.info("Adding education for userId: {}", userId);
        Employee employee = iEmployeeRepo.findByUserId(userId)
                .orElseThrow(() -> new EmployeeNotFoundException1(userId));
 
        EmployeeEducation education = new EmployeeEducation();
        education.setEmployee(employee);          
        education.setDegree(dto.getDegree());
        education.setInstitution(dto.getInstitution());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setPassingYear(dto.getPassingYear());
        education.setPercentage(dto.getPercentage());
        education.setGrade(dto.getGrade());
        education.setLocation(dto.getLocation());
 
        EmployeeEducation saved = educationRepo.save(education);
        logger.info("Education added for userId: {}", userId);
        return mapToResponse(saved);
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeEducationResponseDTO> getMyEducation(Long userId) {
        logger.info("Fetching education for userId: {}", userId);
 
        return educationRepo
                .findByEmployeeUserIdOrderByPassingYearDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    
    @Override
    public EmployeeEducationResponseDTO updateEducation(Integer eduId, EmployeeEducationRequestDTO dto, Long userId) {
 
        logger.info("Updating eduId: {} for userId: {}", eduId, userId);

        EmployeeEducation education = educationRepo
                .findByEduIdAndEmployeeUserId(eduId, userId)
                .orElseThrow(() -> new RuntimeException( "Education record not found or " + "does not belong to you: " + eduId));
 
        if (dto.getDegree() != null && !dto.getDegree().isBlank())
            education.setDegree(dto.getDegree());
        if (dto.getInstitution() != null && !dto.getInstitution().isBlank())
        	education.setInstitution(dto.getInstitution());
        if (dto.getFieldOfStudy() != null)
            education.setFieldOfStudy(dto.getFieldOfStudy());
        if (dto.getPassingYear() != null)
            education.setPassingYear(dto.getPassingYear());
        if (dto.getPercentage() != null)
            education.setPercentage(dto.getPercentage());
        if (dto.getGrade() != null)
            education.setGrade(dto.getGrade());
        if (dto.getLocation() != null)
            education.setLocation(dto.getLocation());
 
        EmployeeEducation updated = educationRepo.save(education);
        logger.info("Education updated: eduId: {}", eduId);
        return mapToResponse(updated);
    }
 
    @Override
    public void deleteEducation(Integer eduId, Long userId) {
        logger.warn("Deleting eduId: {} for userId: {}",eduId, userId);

        EmployeeEducation education = educationRepo
                .findByEduIdAndEmployeeUserId(eduId, userId)
                .orElseThrow(() -> new RuntimeException("Education record not found or " + "does not belong to you: " + eduId));
 
        educationRepo.delete(education);
        logger.info("Education deleted: eduId: {}", eduId);
    }
 

    @Override
    @Transactional
    public List<EmployeeEducationResponseDTO> getEducationByUserId( Long userId) {
        logger.info("HR fetching education for userId: {}", userId);
 
        return educationRepo
                .findByEmployeeUserIdOrderByPassingYearDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
 

    private EmployeeEducationResponseDTO mapToResponse( EmployeeEducation e) {
    	
        return EmployeeEducationResponseDTO.builder()
                .eduId(e.getEduId())
                .userId(e.getEmployee().getUserId())
                .degree(e.getDegree())
                .institution(e.getInstitution())
                .fieldOfStudy(e.getFieldOfStudy())
                .passingYear(e.getPassingYear())
                .percentage(e.getPercentage())
                .grade(e.getGrade())
                .location(e.getLocation())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
