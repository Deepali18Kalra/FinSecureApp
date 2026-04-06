package com.ds.app.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ds.app.entity.Employee;
import com.ds.app.enums.Status;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	
	Optional<Employee>findByUserId(Long userId);
	
	boolean existsByUserId(Long userId);
	
	List<Employee> findByStatus(Status status);
	
	 Page<Employee> findByBankAccountIsNull(Pageable pageable);

	boolean existsByUsername(String string);
	Optional<Employee> findByUsername(String name);

	


	

}
