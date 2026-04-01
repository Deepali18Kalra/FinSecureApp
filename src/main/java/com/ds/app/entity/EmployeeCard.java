package com.ds.app.entity;

import com.ds.app.enums.CardStatus;
import com.ds.app.enums.CardType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
 @NoArgsConstructor @AllArgsConstructor
 @Builder
 @Data
public class EmployeeCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY , targetEntity = Employee.class)    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // stored encrypted — expose only last 4 digits
    @Column(nullable = false, length = 20)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    // stored as YYYY-MM string via YearMonthConverter
    @Column(nullable = false)
    private YearMonth expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate issuedAt;
}