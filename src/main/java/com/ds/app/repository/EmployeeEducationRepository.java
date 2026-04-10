package com.ds.app.repository;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
 
import com.ds.app.entity.EmployeeEducation;
 
@Repository
public interface EmployeeEducationRepository extends JpaRepository<EmployeeEducation, Integer> {
 
	    List<EmployeeEducation> findByEmployeeUserIdOrderByPassingYearDesc(Long userId);
	
	    Optional<EmployeeEducation> findByEduIdAndEmployeeUserId( Integer eduId, Long userId);
	 
	    boolean existsByEmployeeUserId(Long userId);
	 
	    long countByEmployeeUserId(Long userId);

	    @Query("SELECT COUNT(DISTINCT e.employee.userId) " +
	    	       "FROM EmployeeEducation e")
	    	long countDistinctEmployees();

    
}