package com.ds.app.repository;
import com.ds.app.entity.EmployeeCard;
import com.ds.app.enums.CardStatus;
import com.ds.app.enums.CardType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeCardRepository extends JpaRepository<EmployeeCard, Long> {
	
	// All Cards of one employee
	
	List<EmployeeCard>findByEmployee_UserId(Long employeeId);
	
	
	// Filter By Status
	Page<EmployeeCard>findByCardStatus(String cardStatus, Pageable pageable);
	
	// Filter by Card Type
	
	Page<EmployeeCard>findByCardType(String cardType, Pageable pageable);
	
	//filter by employee + status
	Page<EmployeeCard>findByEmployee_UserIdAndCardStatus(Long employeeId, String cardStatus, Pageable pageable);
	
	//duplicate check - same employee , same card type, active status
	boolean existsByEmployee_UserIdAndCardTypeAndCardStatus(Long employeeId, CardType cardType, CardStatus cardStatus);
	
	// count cards by status
	Long countByCardStatus(CardStatus cardStatus);
	
}

