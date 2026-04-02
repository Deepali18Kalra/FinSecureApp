package com.ds.app.controller;

import com.ds.app.dto.request.CardRequestDTO;
import com.ds.app.dto.request.CardStatusUpdateDTO;
import com.ds.app.dto.response.CardResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.enums.CardStatus;
import com.ds.app.enums.CardType;
import com.ds.app.exception.ResourceAlreadyExistException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/finsecure/finance/cards")



public class CardController {

	@Autowired
	private CardService cardService;

	private Long getLoggedInUserId() {
		MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal(); // principal means Currently logged-in user ka actual identity object 
		return userDetails.getUser().getUserId();
	}

	@PostMapping
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ResponseEntity<CardResponseDTO> addCard(@Valid @RequestBody CardRequestDTO dto)
			throws ResourceAlreadyExistException {
		CardResponseDTO response = cardService.registerCard(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ResponseEntity<CardResponseDTO> updateCardStatus(@PathVariable Long id,
			@Valid @RequestBody CardStatusUpdateDTO dto) throws ResourceNotFoundException {
		return ResponseEntity.ok(cardService.updateCardStatus(id, dto));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) throws ResourceNotFoundException {
		return ResponseEntity.ok(cardService.getCardById(id));
	}

	@GetMapping("/employee/{employeeId}")
	@PreAuthorize("hasAuthority('FINANCE')")
	public ResponseEntity<List<CardResponseDTO>> getCardsByEmployee(@PathVariable Long employeeId)
			throws ResourceNotFoundException {
		return ResponseEntity.ok(cardService.getCardsByEmployee(employeeId));
	}

	@GetMapping("/summary")
	@PreAuthorize("hasAuthority('FINANCE')")
	public ResponseEntity<Map<String, Long>> getCardSummary() {
		return ResponseEntity.ok(cardService.getCardStatistics());
	}
}