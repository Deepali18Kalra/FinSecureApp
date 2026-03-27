package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ds.app.entity.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Integer>{
	Optional<Employee> findByUserId(Integer userId);
	Optional<Employee> findByUsername(String username);
}
