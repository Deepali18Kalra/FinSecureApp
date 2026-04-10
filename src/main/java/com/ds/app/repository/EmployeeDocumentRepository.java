package com.ds.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.EmployeeDocument;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Integer> {
			
	
			List<EmployeeDocument> findByEmployeeUserId(Long userId);
			
			Optional<EmployeeDocument> findByDocumentIdAndEmployeeUserId(Integer documentId, Long userId);
			
			boolean existsByEmployeeUserIdAndDocumentType(Long userId, String documentType);

			boolean existsByEmployeeUserId(Long userId);

			@Query("SELECT COUNT(DISTINCT d.employee.userId) " +
				       "FROM EmployeeDocument d")
				long countDistinctEmployees();
		

}//end class