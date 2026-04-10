package com.ds.app.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.PagedResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.utils.MaskingUtil;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.AppUserRepository;
import com.ds.app.service.EmployeeAdminService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EmployeeAdminServiceImpl  implements EmployeeAdminService{
	
	@Autowired
	EmployeeRepository iEmployeeRepo;
	
	@Autowired
	AppUserRepository appUserRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeAdminServiceImpl.class);

			@Override
			public void softDeleteEmployee(Long userId) throws Exception {
					 logger.warn("Soft delete for userId: {}", userId);
				        Employee employee = iEmployeeRepo.findByUserId(userId)
				                .orElseThrow(() -> new EmployeeNotFoundException1(userId));
				        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
				            logger.info("userId: {} already deleted — no action", userId);
				            return;
				        }
				        employee.setIsDeleted(true);
				        iEmployeeRepo.save(employee);
				        logger.info("Soft deleted userId: {}", userId);
				
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
		       dto.setDesignation(e.getDesignation());
		       dto.setIsDeleted(e.getIsDeleted());
		       dto.setIsAccountLocked(e.getIsAccountLocked());
		       dto.setIsEscalated(e.getIsEscalated());
		       dto.setCreatedAt(e.getCreatedAt());
		       dto.setUpdatedAt(e.getUpdatedAt());
		       return dto;
		   }
		    
		    @Override
		    @Transactional
		    public void restoreEmployee(Long userId) throws EmployeeNotFoundException1 {
		        logger.info("Restoring employee for userId: {}", userId);
		        Employee employee = iEmployeeRepo.findByUserId(userId)
		                .orElseThrow(() -> new EmployeeNotFoundException1(userId));
		
		        if (Boolean.FALSE.equals(employee.getIsDeleted())) {
		            logger.info("Employee is not deleted for userId: {}", userId);
		            throw new RuntimeException("Employee is not deleted for userId: " + userId);
		        }
		        employee.setIsDeleted(false);
		        iEmployeeRepo.save(employee);
		        logger.info("Employee restored for userId: {}", userId);
		    }
		    
		    
		    @Override
		    @Transactional
		    public PagedResponseDTO<EmployeeProfileResponseDTO> findDeletedEmployees(Pageable pageable) {
		        logger.info("Fetching deleted employees");
		
		        Page<Employee> page = iEmployeeRepo.findByIsDeletedTrue(pageable);
		        List<EmployeeProfileResponseDTO> data = page.getContent()
		                .stream()
		                .map(this::mapToMaskedResponseDTO)
		                .toList();
		
		        logger.info("Found {} deleted employees", data.size());
		        return PagedResponseDTO.of(data, page);
		    }

}//end class
