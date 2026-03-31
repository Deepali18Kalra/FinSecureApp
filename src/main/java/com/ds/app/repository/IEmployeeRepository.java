package com.ds.app.repository;

import com.ds.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IEmployeeRepository extends JpaRepository<Employee, Long>{
	Optional<Employee> findByUserId(Long userId);
	Optional<Employee> findByUsername(String username);
}
