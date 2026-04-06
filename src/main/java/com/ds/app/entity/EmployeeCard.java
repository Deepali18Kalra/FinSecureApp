package com.ds.app.entity;

import java.time.LocalDate;
import java.time.YearMonth;

import com.ds.app.enums.CardStatus;
import com.ds.app.enums.CardType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EmployeeCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "employee_id", nullable = false)
	private Long employeeId;

	@Column(nullable = false, length = 20)
	private String cardNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CardType cardType;

	@Column(nullable = false)
	private YearMonth expiryDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CardStatus cardStatus = CardStatus.ACTIVE;

	@Column(nullable = false)
	private LocalDate issuedAt;
}
