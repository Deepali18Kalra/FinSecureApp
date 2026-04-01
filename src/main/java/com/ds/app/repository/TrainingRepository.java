package com.ds.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.Training;
import com.ds.app.enums.TrainingStatus;

@Repository
public interface TrainingRepository  extends JpaRepository<Training, Long>  {
	
	//find all training by status
	List<Training> findByStatus(TrainingStatus status);
	
	Optional<Training> findById(Long id);

	Page<Training> findByIsDeletedFalse(Pageable pageable);

	Optional<Training> findByTrainingNameAndStartDate(String trainingName, LocalDate startDate);
	

	
	
	
	
	

}
