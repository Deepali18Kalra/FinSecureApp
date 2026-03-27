package com.ds.app.entity;

import java.time.LocalDate;

import com.ds.app.enums.HolidayType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long holidayId;
	
	@Column(unique = true)
	private LocalDate date;
	private String name;
	
	@Enumerated(EnumType.STRING)
	private HolidayType type;
}
