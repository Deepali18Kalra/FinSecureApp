package com.ds.app.repository;

import com.ds.app.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface IHolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("select h.date from Holiday h where h.date between :startDate and :endDate")
    Set<LocalDate> findDatesBetween(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);}