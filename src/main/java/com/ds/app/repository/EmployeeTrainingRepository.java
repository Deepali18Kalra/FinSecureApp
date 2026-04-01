package com.ds.app.repository;




import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.EmployeeTraining;
import com.ds.app.enums.EnrollmentStatus;


@Repository
public interface EmployeeTrainingRepository extends JpaRepository<EmployeeTraining, Long> {
	
	//find all enrollment for one training
	List<EmployeeTraining> findByTraining_TrainingId(Long trainingId);
	


	Long countByTraining_TrainingId(Long trainingId);
	
	boolean existsByEmployee_UserIdAndTraining_TrainingId(Long employeeId, Long trainingId);



	Page<EmployeeTraining> findByEmployee_UserId(Long userId, Pageable pageable);



	List<EmployeeTraining> findByEmployee_UserId(long longValue);



	Page<EmployeeTraining> findByTraining_TrainingId(Long trainingId, Pageable pageable);



   Boolean existsByEmployee_UserIdAndTraining_TrainingIdAndStatus(Long employeeId, Long trainingId,
			EnrollmentStatus status);


  
	

}
