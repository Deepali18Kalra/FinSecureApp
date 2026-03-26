package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ds.app.entity.Employee;
import com.ds.app.entity.Training;



public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

	Optional<Employee> findByUsername(String username);
	boolean existsByUsername(String string);
	
	  // eligible employees — FRESHER or NON_CERTIFIED or NON_SKILLED
    @Query("SELECT e FROM Employee e WHERE " +
           "e.certificationStatus = 'NON_CERTIFIED' OR " +
           "e.skillStatus = 'NON_SKILLED' OR " +
           "e.employeeExperience = 'FRESHER'")
    Page<Employee> findEligibleForTraining(Pageable pageable);

    // eligible filtered by department
    @Query("SELECT e FROM Employee e WHERE " +
           "(e.certificationStatus = 'NON_CERTIFIED' OR " +
           "e.skillStatus = 'NON_SKILLED' OR " +
           "e.employeeExperience = 'FRESHER') " +
           "AND e.departmentId = :deptId")
     Page<Employee> findEligibleByDepartment(  @Param("deptId") Long deptId,
            Pageable pageable);
    
	Optional<Employee> findByUserId(Long employeeId);


}
