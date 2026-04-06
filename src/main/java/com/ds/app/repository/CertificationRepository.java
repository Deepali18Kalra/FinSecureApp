package com.ds.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.Certification;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long>{

	List<Certification> findByVerifiedByHrFalse();
	
	
	
//	 @Query("SELECT CASE WHEN COUNT(et) > 0 THEN TRUE ELSE FALSE END " +
//	           "FROM EmployeeTraining et " +
//	           "WHERE et.employee.userId = :userId AND et.training.trainingId = :trainingId")
//	    boolean existsByEmployeeAndTraining(@Param("userId") Long userId,
//	                                        @Param("trainingId") Long trainingId);

	boolean existsByEmployee_UserIdAndTraining_TrainingId(Long userId, Long trainingId);

	Page<Certification> findByEmployee_UserId(Long userId, Pageable pageable);

//	     findByEmployee_UserId(Long userId, Pageable pageable);

}
