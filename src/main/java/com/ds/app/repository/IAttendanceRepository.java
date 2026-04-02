package com.ds.app.repository;

import com.ds.app.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAttendanceRepository extends JpaRepository<Attendance, Long>{
	Optional<Attendance> findByEmployeeUserIdAndDate(Long employeeId, LocalDate date);
	Page<Attendance> findByEmployeeUserId(Long employeeId, Pageable pageable);

    Boolean existsByDate(LocalDate date);
	
	@Query("""
			select a 
			from Attendance a 
			where a.employee.userId =:employeeId
			and MONTH(a.date) =:month
			and YEAR(a.date) =:year
			""")
	List<Attendance> findAttendanceByEmployeeUserIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
	
	@Query("""
			select a 
			from Attendance a 
			where a.employee.userId =:employeeId
			and (:month is null or MONTH(a.date) =:month)
			and (:year is null or YEAR(a.date) =:year)
			""")
	Page<Attendance> findAttendanceByEmployeeUserIdAndMonthAndYear(Long employeeId, Integer month, Integer year, Pageable pageable);
	
	Page<Attendance> findByEmployee_Manager_UserIdAndDate(Long hrId, LocalDate date, Pageable pageable);
	
//	@Query("""
//			select new com.ds.app.dto.MonthlyAttendanceReport(
//				a.employee.userId,
//				concat(a.employee.firstName, a.employee.lastName),
//				Month(a.date),
//				Year(a.date),
//				sum(case when a.status = 'PRESENT' then 1 else 0 end) + 
//				sum(case when a.status = 'MANUAL_PUNCH' and a.hoursWorked >= 4 then 1 else 0 end),
//				sum(case when a.status = 'ABSENT' then 1 else 0 end ),
//				sum(case when a.status = 'LATE' then 1 else 0 end),
//				sum(case when a.status = 'HALF_DAY_PRESENT' then 1 else 0 end) + 
//				sum(case when a.status = 'MANUAL_PUNCH' and a.hoursWorked < 4 then 1 else 0 end),
//				count(hoursWorked)
//			)
//			from Attendance a
//			where a.Employee.userId =: employeeId
//			and Month(a.date) =:month
//			and Year(a.date) =:year
//			""")
//	MonthlyAttendanceReport findMonthlyReportByEmployeeUserIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
}
