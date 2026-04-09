package com.ds.app.repository;

import com.ds.app.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IHolidayRepository extends JpaRepository<Holiday, Long> {

    // used internally by LeaveService and LeaveBalanceScheduler
    @Query("select h.date from Holiday h where h.date between :startDate and :endDate")
    Set<LocalDate> findDatesBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("select h from Holiday h where year(h.date) = :year order by h.date asc")
    List<Holiday> findByYear(@Param("year") Integer year);

    boolean existsByDate(LocalDate date);

    Optional<Holiday> findById(Long holidayId);
}