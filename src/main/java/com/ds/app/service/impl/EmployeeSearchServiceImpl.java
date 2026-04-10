package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.*;
import com.ds.app.dto.response.*;
import com.ds.app.entity.Employee;
import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.utils.MaskingUtil;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeRewardRepository;
import com.ds.app.repository.AppUserRepository;
import com.ds.app.repository.EmployeeDocumentRepository;
import com.ds.app.repository.EmployeeEducationRepository;
import com.ds.app.service.EmployeeSearchService;

import jakarta.transaction.Transactional;

@Service
public class EmployeeSearchServiceImpl implements EmployeeSearchService {
	
	@Autowired
	EmployeeRepository iEmployeeRepo;
	
	@Autowired
	AppUserRepository appUserRepository;
	
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	EmployeeEducationRepository educationRepo;
	
	@Autowired
	EmployeeDocumentRepository documentRepo;
	
	@Autowired
	EmployeeRewardRepository rewardRepo;
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeSearchServiceImpl.class);

	@Override
	public PagedResponseDTO<EmployeeProfileResponseDTO> filterUsers(EmployeeFilterRequestDTO dto, Pageable pageable) {
		logger.info("Filtering employees — filter: {}", dto);
		 
        Page<Employee> page = iEmployeeRepo.filterEmployees(
                dto.getFirstName(),
                dto.getDesignation(),
                dto.getEmploymentType(),
                dto.getEmployeeExperience(),
                dto.getIsDeleted(),
                dto.getIsAccountLocked(),
                pageable);
 
        List<EmployeeProfileResponseDTO> data = page.getContent()
                .stream()
                .map(this::mapToMaskedResponseDTO)
                .toList();
 
        logger.info("Filter returned {} results", data.size());
        return PagedResponseDTO.of(data, page);
	}



	  private EmployeeProfileResponseDTO mapToMaskedResponseDTO(Employee e) {
	        EmployeeProfileResponseDTO dto = new EmployeeProfileResponseDTO();
	        dto.setUserId(e.getUserId());
	        dto.setUsername(e.getUsername());
	        dto.setRole(e.getRole());
	        dto.setEmployeeCode(e.getEmployeeCode());
	        dto.setFirstName(e.getFirstName());
	        dto.setLastName(e.getLastName());
	        dto.setEmail(MaskingUtil.maskEmail(e.getEmail()));
	        dto.setPhoneNumber(MaskingUtil.maskPhone(e.getPhoneNumber()));
	        dto.setDateOfBirth(e.getDateOfBirth());
	        dto.setGender(e.getGender());
	        dto.setDesignation(e.getDesignation());
	        dto.setJoiningDate(e.getJoiningDate());
	        dto.setEmploymentType(e.getEmploymentType());
	     
	        dto.setAddressLine(e.getAddressLine());
	        dto.setCity(e.getCity());
	        dto.setState(e.getState());
	        dto.setCountry(e.getCountry());
	        dto.setPincode(e.getPincode());
	        dto.setIsDeleted(e.getIsDeleted());
	        dto.setIsAccountLocked(e.getIsAccountLocked());
	        dto.setIsEscalated(e.getIsEscalated());
	        dto.setProfilePhotoUrl(e.getProfilePhotoUrl());
	        dto.setCreatedAt(e.getCreatedAt());
	        dto.setUpdatedAt(e.getUpdatedAt());
	        dto.setIsProfileComplete(isProfileComplete(e));
	        return dto;
	    }
	 
	  private Boolean isProfileComplete(Employee e) {
		    return e.getFirstName()   != null && !e.getFirstName().isBlank()
		        && e.getLastName()    != null && !e.getLastName().isBlank()
		        && e.getEmail()       != null && !e.getEmail().isBlank()
		        && e.getPhoneNumber() != null && !e.getPhoneNumber().isBlank()
		        && e.getDateOfBirth() != null
		        && e.getGender()      != null
		        && e.getAddressLine() != null && !e.getAddressLine().isBlank()
		        && e.getCity()        != null && !e.getCity().isBlank()
		        && e.getState()       != null && !e.getState().isBlank()
		        && e.getCountry()     != null && !e.getCountry().isBlank()
		        && e.getPincode()     != null && !e.getPincode().isBlank()
		    	&& e.getProfilePhotoUrl() != null;
		}
	  
	  

	  @Override
	  public CountReportDTO getCountReport() {
	      logger.info("Generating count report");

	      long totalActive = iEmployeeRepo.countByIsDeletedFalse();

	      CountReportDTO report = CountReportDTO.builder()

	              // Employee status
	              .totalDeleted(iEmployeeRepo.countByIsDeletedTrue())

	              // Profile completion
	              .incompleteProfiles(iEmployeeRepo.countIncompleteProfiles())
	              .withoutPhoto(iEmployeeRepo.countByProfilePhotoUrlIsNullAndIsDeletedFalse())

	              // Education
	              .withEducation(educationRepo.countDistinctEmployees())
	              .withoutEducation(totalActive - educationRepo.countDistinctEmployees())

	              // Documents
	              .withDocuments(documentRepo.countDistinctEmployees())
	              .withoutDocuments(totalActive - documentRepo.countDistinctEmployees())

	              // Rewards
	              .withRewards(rewardRepo.countDistinctEmployees())
	              .build();

	      logger.info("Count report generated successfully");
	      return report;
	  }

	  @Override
	  public PagedResponseDTO<EmployeeProfileResponseDTO> getRecentlyJoined(int days, Pageable pageable) {
		  logger.info("Fetching employees joined in last {} days", days);
		  
	        LocalDate fromDate = LocalDate.now().minusDays(days);
	        logger.info("Fetching employees joined since: {}", fromDate);
	 
	        Page<Employee> page = iEmployeeRepo.findRecentlyJoined(
	                fromDate, pageable);
	 
	        List<EmployeeProfileResponseDTO> data = page.getContent()
	                .stream()
	                .map(this::mapToMaskedResponseDTO)
	                .toList();
	 
	        logger.info("Found {} employees joined since {}",
	                data.size(), fromDate);
	 
	        return PagedResponseDTO.of(data, page);
	  }

	  @Override
	  @Transactional
	  public PagedResponseDTO<EmployeeSimpleResponseDTO> getEmployeesWithoutPhoto(Pageable pageable) {
		  
		  logger.info("Fetching employees without photo");
		    Page<Employee> page = iEmployeeRepo.findEmployeesWithoutPhoto(pageable);
		    List<EmployeeSimpleResponseDTO> data = page.getContent()
		            .stream()
		            .map(this::mapToSimpleDTO)  // ← simple mapper
		            .toList();
		    logger.info("Found {} employees without photo",data.size());
		    return PagedResponseDTO.of(data, page);
		    
		  }
	  
	  private EmployeeSimpleResponseDTO mapToSimpleDTO( Employee e) {
		    String fullName = null;
		    if (e.getFirstName() != null && e.getLastName() != null)
		        fullName = e.getFirstName() + " " + e.getLastName();
		    else if (e.getFirstName() != null)
		        fullName = e.getFirstName();

		    return EmployeeSimpleResponseDTO.builder()
		            .userId(e.getUserId())
		            .employeeCode(e.getEmployeeCode())
		            .fullName(fullName)
		            .build();
		}

	  @Override
	  @Transactional
	  public PagedResponseDTO<EmployeeIncompleteResponseDTO> getIncompleteProfiles(Pageable pageable) {
		  
		  logger.info("Fetching incomplete profiles");
		    Page<Employee> page = iEmployeeRepo.findIncompleteProfiles(pageable);
		    List<EmployeeIncompleteResponseDTO> data =
		            page.getContent()
		            .stream()
		            .map(this::mapToIncompleteDTO)  // ← simple mapper
		            .toList();
		    logger.info("Found {} incomplete profiles",data.size());
		    return PagedResponseDTO.of(data, page);

	  }
	  
	  private EmployeeIncompleteResponseDTO mapToIncompleteDTO(Employee e) {

			  // Full name
			  String fullName = null;
			  if (e.getFirstName() != null &&
			          e.getLastName() != null)
			      fullName = e.getFirstName() + " " +
			              e.getLastName();
			  else if (e.getFirstName() != null)
			      fullName = e.getFirstName();
			
			  // Missing profile fields
			  List<String> missingFields = new ArrayList<>();
			  if (e.getFirstName() == null ||
			          e.getFirstName().isBlank())
			      missingFields.add("firstName");
			  if (e.getLastName() == null ||
			          e.getLastName().isBlank())
			      missingFields.add("lastName");
			  if (e.getEmail() == null ||
			          e.getEmail().isBlank())
			      missingFields.add("email");
			  if (e.getPhoneNumber() == null ||
			          e.getPhoneNumber().isBlank())
			      missingFields.add("phoneNumber");
			  if (e.getDateOfBirth() == null)
			      missingFields.add("dateOfBirth");
			  if (e.getGender() == null)
			      missingFields.add("gender");
			  if (e.getAddressLine() == null ||
			          e.getAddressLine().isBlank())
			      missingFields.add("addressLine");
			  if (e.getCity() == null ||
			          e.getCity().isBlank())
			      missingFields.add("city");
			  if (e.getState() == null ||
			          e.getState().isBlank())
			      missingFields.add("state");
			  if (e.getCountry() == null ||
			          e.getCountry().isBlank())
			      missingFields.add("country");
			  if (e.getPincode() == null ||
			          e.getPincode().isBlank())
			      missingFields.add("pincode");
			  if (e.getProfilePhotoUrl() == null)
			      missingFields.add("profilePhoto");
			
			  // Education and document check
			  boolean hasEducation = educationRepo
			          .existsByEmployeeUserId(e.getUserId());
			  boolean hasDocuments = documentRepo
			          .existsByEmployeeUserId(e.getUserId());
			
			  return EmployeeIncompleteResponseDTO.builder()
			          .userId(e.getUserId())
			          .employeeCode(e.getEmployeeCode())
			          .fullName(fullName)
			          .missingFields(missingFields)
			          .hasEducation(hasEducation)
			          .hasDocuments(hasDocuments)
			          .build();
			}

	  @Override
	  @Transactional
	  public List<MonthlyStatDTO> getMonthlyStats(int year) {
	      logger.info("Fetching monthly stats for year: {}", year);
	   
	      List<Object[]> results = iEmployeeRepo.countByMonthAndYear(year);
	  
	      Map<Integer, Long> monthCountMap = new HashMap<>();
	      for (Object[] row : results) {
	          int month = ((Number) row[0]).intValue();
	          long count = ((Number) row[1]).longValue();
	          monthCountMap.put(month, count);
	      }
	   
	      List<MonthlyStatDTO> stats = new ArrayList<>();
	      String[] monthNames = {
	          "January", "February", "March", "April",
	          "May", "June", "July", "August",
	          "September", "October", "November", "December"
	      };
	   
	      for (int i = 1; i <= 12; i++) {
	          stats.add(MonthlyStatDTO.builder()
	                  .year(year)
	                  .month(i)
	                  .monthName(monthNames[i - 1])
	                  .count(monthCountMap.getOrDefault(i, 0L))
	                  .build());
	      }
	   
	      logger.info("Monthly stats for {}: {} months returned",year, stats.size());
	      return stats;
	  }
	   
	  @Override
	  @Transactional
	  public List<YearlyStatDTO> getYearlyStats() {
	      logger.info("Fetching yearly stats");
	   
	      List<Object[]> results = iEmployeeRepo.countByYear();
	   
	      List<YearlyStatDTO> stats = results.stream()
	    		  .filter(row -> row[0] != null) 
	              .map(row -> YearlyStatDTO.builder()
	                      .year(((Number) row[0]).intValue())
	                      .count(((Number) row[1]).longValue())
	                      .build())
	              .collect(Collectors.toList());
	   
	      logger.info("Yearly stats: {} years returned", stats.size());
	      return stats;
	      
	  }
	

}//end class
