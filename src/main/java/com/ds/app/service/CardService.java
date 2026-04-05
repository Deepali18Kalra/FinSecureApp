package com.ds.app.service;

import java.util.List;
import java.util.Map;

import com.ds.app.dto.request.CardRequestDTO;
import com.ds.app.dto.request.CardStatusUpdateDTO;
import com.ds.app.dto.response.CardResponseDTO;
import com.ds.app.exception.ResourceAlreadyExistException;
import com.ds.app.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public interface CardService {
	
	
	// finance register a card for the employee
	CardResponseDTO registerCard(CardRequestDTO cardRequest,Long empid) throws ResourceAlreadyExistException;
	
	// finance can update card status (block/unblock)
	CardResponseDTO updateCardStatus(Long cardId, CardStatusUpdateDTO dto) throws ResourceNotFoundException;
	
	// Get a single card by Id
	CardResponseDTO getCardById(Long cardId) throws ResourceNotFoundException;
	
	// Get All Cards of one employee
	List<CardResponseDTO>getCardsByEmployee(Long employeeId) throws ResourceNotFoundException;
	
	Map<String , Long > getCardStatistics(); // returns count of cards by status and type
	
	
}
