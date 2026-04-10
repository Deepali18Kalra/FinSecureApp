package com.ds.app.service;

import org.springframework.data.domain.Pageable;

import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.PagedResponseDTO;
import com.ds.app.exception.EmployeeNotFoundException1;


public interface EmployeeAdminService {
	
	    public void softDeleteEmployee(Long userId) throws Exception;
	
	    public void restoreEmployee(Long userId) throws EmployeeNotFoundException1;
	    
	    PagedResponseDTO<EmployeeProfileResponseDTO> findDeletedEmployees(Pageable pageable);
    
}//endclass
