package com.ds.app.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import com.ds.app.dto.request.*;
import com.ds.app.dto.response.*;

public interface EmployeeSearchService {
	
    PagedResponseDTO<EmployeeProfileResponseDTO> filterUsers(EmployeeFilterRequestDTO dto, Pageable pageable);
   
    CountReportDTO getCountReport();

    PagedResponseDTO<EmployeeProfileResponseDTO> getRecentlyJoined( int days, Pageable pageable);
    
    PagedResponseDTO<EmployeeSimpleResponseDTO> getEmployeesWithoutPhoto(Pageable pageable);
    
    PagedResponseDTO<EmployeeIncompleteResponseDTO> getIncompleteProfiles(Pageable pageable);
 
	List<MonthlyStatDTO> getMonthlyStats(int year);

	List<YearlyStatDTO> getYearlyStats();


}//end class
