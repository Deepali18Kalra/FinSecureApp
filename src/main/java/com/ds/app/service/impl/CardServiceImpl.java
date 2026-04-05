package com.ds.app.service.impl;

import com.ds.app.dto.request.CardRequestDTO;
import com.ds.app.dto.request.CardStatusUpdateDTO;
import com.ds.app.dto.response.CardResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeCard;
import com.ds.app.enums.CardStatus;
import com.ds.app.exception.ResourceAlreadyExistException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.EmployeeCardRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.CardService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService {

	@Autowired
	private EmployeeCardRepository cardRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public CardResponseDTO registerCard(CardRequestDTO dto,Long empId) throws ResourceAlreadyExistException {
		// 1 — validate employee exists
		Employee employee = employeeRepository.findByUserId(empId).orElseThrow(
				() -> new ResourceAlreadyExistException("Employee not found with id: " + empId));

		// 2 — duplicate check: employee already has an ACTIVE card of the same type
		boolean duplicate = cardRepository.existsByEmployee_UserIdAndCardTypeAndCardStatus(empId,
				dto.getCardType(), CardStatus.ACTIVE);
		if (duplicate) {
			throw new ResourceAlreadyExistException("Employee already has an ACTIVE " + dto.getCardType() + " card. "
					+ "Block the existing card before registering a new one.");
		}

		// 3 — parse and validate expiry date
		YearMonth expiryDate = YearMonth.parse(dto.getExpiryDate());
		if (!expiryDate.isAfter(YearMonth.now())) {
			throw new IllegalArgumentException("Card expiry date must be in the future.");
		}

		// 4 — strip formatting characters, keep digits only
		String rawCardNumber = dto.getCardNumber().replaceAll("[\\s-]", "");

		// 5 — build and save
		EmployeeCard card = EmployeeCard.builder().employee(employee).cardNumber(rawCardNumber)
				.cardType(dto.getCardType()).expiryDate(expiryDate).cardStatus(CardStatus.ACTIVE).build();
		card.setIssuedAt(LocalDate.now());

		return mapToResponse(cardRepository.save(card));

	}

	@Override
	public CardResponseDTO updateCardStatus(Long cardId, CardStatusUpdateDTO dto) throws ResourceNotFoundException {
		EmployeeCard card = cardRepository.findById(cardId)
				.orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

		// Cannot reactivate an EXPIRED card
		if (card.getCardStatus() == CardStatus.EXPIRED && dto.getCardStatus() == CardStatus.ACTIVE) {
			throw new IllegalStateException("An EXPIRED card cannot be reactivated.");
		}

		card.setCardStatus(dto.getCardStatus());
		return mapToResponse(cardRepository.save(card));
	}

	@Override
	public CardResponseDTO getCardById(Long cardId) throws ResourceNotFoundException {
		EmployeeCard card = cardRepository.findById(cardId)
				.orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
		return mapToResponse(card);
	}

	@Override
	public List<CardResponseDTO> getCardsByEmployee(Long employeeId) throws ResourceNotFoundException {
		employeeRepository.findByUserId(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

		return cardRepository.findByEmployee_UserId(employeeId).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, Long> getCardStatistics() {
		Map<String, Long> summary = new HashMap<>();
		summary.put("TOTAL", cardRepository.count());
		summary.put("ACTIVE", cardRepository.countByCardStatus(CardStatus.ACTIVE));
		summary.put("BLOCKED", cardRepository.countByCardStatus(CardStatus.BLOCKED));
		summary.put("EXPIRED", cardRepository.countByCardStatus(CardStatus.EXPIRED));
		return summary;
	}

	private CardResponseDTO mapToResponse(EmployeeCard card) {
		return CardResponseDTO.builder().id(card.getId()).employeeId(card.getEmployee().getUserId())
				.employeeName(card.getEmployee().getFirstName() + " " + card.getEmployee().getLastName())
				.cardNumberMasked(maskCardNumber(card.getCardNumber())).cardType(card.getCardType())
				.expiryDate(card.getExpiryDate() != null ? card.getExpiryDate().toString() : null)
				.cardStatus(card.getCardStatus()).issuedAt(card.getIssuedAt()).build();
	}

	/**
	 * "1234567890123456" → "**** **** **** 3456"
	 */
	private String maskCardNumber(String cardNumber) {
		if (cardNumber == null || cardNumber.length() < 4)
			return "****";
		String last4 = cardNumber.substring(cardNumber.length() - 4);
		return "**** **** **** " + last4;
	}
}
