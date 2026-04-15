package com.ds.app.entity;

import com.ds.app.enums.HolidayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long holidayId;
	
	@Column(unique = true, nullable = false)
	private LocalDate date;

    @Column(nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private HolidayType type;
}
