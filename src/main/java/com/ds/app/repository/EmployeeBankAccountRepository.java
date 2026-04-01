package com.ds.app.repository;
import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.enums.BankValidationStatus;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeBankAccountRepository extends JpaRepository<EmployeeBankAccount, Long> {
	
	boolean existsByEmployee_UserId(Long employeeId);
	
	Optional<EmployeeBankAccount> findByEmployee_UserId(Long employeeId);
	
	Page<EmployeeBankAccount>findByValidationStatus(BankValidationStatus validationStatus, Pageable pageable);
	

	
	Page<EmployeeBankAccount>findByBank_BankId(Long bankId, Pageable pageable);
	
	@Query("SELECT e FROM EmployeeBankAccount e WHERE LOWER(e.accountHolderName) LIKE CONCAT('%', LOWER(:name), '%')")
	Page<EmployeeBankAccount> searchByAccountHolderName(@Param("name") String name, Pageable pageable);
	
	long countByValidationStatus(BankValidationStatus validationStatus);
	
	
}
