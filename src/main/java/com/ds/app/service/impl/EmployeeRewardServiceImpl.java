package com.ds.app.service.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.EmployeeRewardRequestDTO;
import com.ds.app.dto.response.EmployeeRewardResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeReward;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeRewardRepository;
import com.ds.app.service.EmployeeRewardService;

import jakarta.transaction.Transactional;

@Service
public class EmployeeRewardServiceImpl implements EmployeeRewardService {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeRewardServiceImpl.class);
	
	@Autowired
	EmployeeRepository employeeRepo;
	
	@Autowired
	EmployeeRewardRepository employeeRewardRepo;
	
	@Override
	public EmployeeRewardResponseDTO giveReward(Long userId, EmployeeRewardRequestDTO dto, String hrUsername) throws Exception {
		
		logger.info("HR '{}' giving eward to userId: {} ", hrUsername, userId);
		
		Employee employee = employeeRepo
				.findByUserIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new EmployeeNotFoundException1(userId));
		
		EmployeeReward reward = new EmployeeReward();
		reward.setEmployee(employee);
		reward.setRewardType(dto.getRewardType());
		reward.setRewardCategory(dto.getRewardCategory());
		reward.setTitle(dto.getTitle());
		reward.setDescription(dto.getDescription());
		reward.setRewardDate(dto.getRewardDate());
		reward.setGivenBy(hrUsername);
		
		EmployeeReward saved = employeeRewardRepo.save(reward);
		logger.info("Reward given to userId: {}", userId);
		return mapToResponse(saved);
		
	}

	private EmployeeRewardResponseDTO mapToResponse(EmployeeReward r) {
		
		return EmployeeRewardResponseDTO.builder()
				.rewardId(r.getRewardId())
				.userId(r.getEmployee().getUserId())
				.rewardType(r.getRewardType())
				.rewardCategory(r.getRewardCategory())
				.rewardDate(r.getRewardDate())
				.title(r.getTitle())
				.description(r.getDescription())
				.givenBy(r.getGivenBy())
				.createdAt(r.getCreatedAt())
				.updatedAt(r.getUpdatedAt())
				.build();
		
	}


	@Override
	public void deleteReward(Integer rewardId) {
		
		logger.warn("Deleting rewardId: {} ", rewardId);
		
		EmployeeReward reward = employeeRewardRepo.findById(rewardId)
				.orElseThrow(() -> new RuntimeException("Reward Not Found:" + rewardId));
		
		employeeRewardRepo.delete(reward);
		logger.info("Reward deleted: {}", rewardId);
				
		
	}
	
	@Override
	@Transactional
	public List<EmployeeRewardResponseDTO> getMyRewards(Long userId) {
		
		logger.info("Fetching rewards for userId: {}", userId);
		
		return employeeRewardRepo
				.findByEmployeeUserIdOrderByRewardDateDesc(userId)
				.stream()
				.map(this::mapToResponse)
				.toList();
		
	}
	
	@Override
	@Transactional
	public List<EmployeeRewardResponseDTO> getRewardsByUserId(Long userId) {
			
		logger.info("HR fetching rewards for userId: {}", userId);
		
		return employeeRewardRepo
				.findByEmployeeUserIdOrderByRewardDateDesc(userId)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Override
	public List<Object[]> getTopRewardedEmployees() {
		
		logger.info("Fetching top rewarded employees");
		return employeeRewardRepo.findTopRewardedEmployee();
		
	}

	@Override
	public EmployeeRewardResponseDTO updateReward(Integer rewardId, EmployeeRewardRequestDTO dto) {
		
		logger.info("Update rewardId: {}", rewardId);
				
				EmployeeReward reward = employeeRewardRepo.findById(rewardId)
						.orElseThrow(() -> new RuntimeException("Reward Not Found: " + rewardId));
				
				if(dto.getRewardType() != null)
					reward.setRewardType(dto.getRewardType());
				if(dto.getRewardCategory() != null)
					reward.setRewardCategory(dto.getRewardCategory());
				if(dto.getGivenBy() != null)
					reward.setGivenBy(dto.getGivenBy());
				if(dto.getTitle() != null && !dto.getTitle().isBlank())
					reward.setTitle(dto.getTitle());
				if(dto.getDescription() != null)
					reward.setDescription(dto.getDescription());
				if(dto.getRewardDate() != null)
					reward.setRewardDate(dto.getRewardDate());
				
				EmployeeReward updated = employeeRewardRepo.save(reward);
				logger.info("Reward updated: {}", rewardId);
				return mapToResponse(updated);
		
		
	}

}//end class
