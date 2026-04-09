package com.ds.app.repository;

import com.ds.app.dto.response.MonthlyAttendanceReport;
import com.ds.app.dto.response.TeamAttendanceReportRow;
import com.ds.app.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAttendanceRepository extends JpaRepository<Attendance, Long>{
	Optional<Attendance> findByEmployeeUserIdAndDate(Long employeeId, LocalDate date);
	Page<Attendance> findByEmployeeUserId(Long employeeId, Pageable pageable);

    Boolean existsByEmployee_UserIdAndDate(Long employeeId, LocalDate date);
	
	@Query("""
			select a 
			from Attendance a 
			where a.employee.userId =:employeeId
			and MONTH(a.date) =:month
			and YEAR(a.date) =:year
			""")
	List<Attendance> findAttendanceByEmployeeUserIdAndMonthAndYear(@Param("employeeId") Long employeeId, @Param("month") Integer month, @Param("year") Integer year);
	
	@Query("""
			select a 
			from Attendance a 
			where a.employee.userId =:employeeId
			and (:month is null or MONTH(a.date) =:month)
			and (:year is null or YEAR(a.date) =:year)
			""")
	Page<Attendance> findAttendanceByEmployeeUserIdAndMonthAndYear(@Param("employeeId") Long employeeId, @Param("month") Integer month, @Param("year") Integer year, Pageable pageable);
	
	Page<Attendance> findByEmployee_Manager_UserIdAndDate(Long managerId, LocalDate date, Pageable pageable);

    @Query("""
    select new com.ds.app.dto.response.MonthlyAttendanceReport(
       a.employee.userId,
       concat(a.employee.firstName, ' ', a.employee.lastName),
       :month,
       :year,
       (sum(case when a.status = com.ds.app.enums.AttendanceStatus.PRESENT then 1 else 0 end) +
        sum(case when a.status = com.ds.app.enums.AttendanceStatus.MANUAL_PUNCH and a.totalMinutesWorked >= 240 then 1 else 0 end)),
       sum(case when a.status = com.ds.app.enums.AttendanceStatus.ABSENT then 1 else 0 end),
       sum(case when a.isLate = true then 1 else 0 end),
       (sum(case when a.status = com.ds.app.enums.AttendanceStatus.HALF_DAY_PRESENT then 1 else 0 end) +
        sum(case when a.status = com.ds.app.enums.AttendanceStatus.MANUAL_PUNCH and a.totalMinutesWorked < 240 then 1 else 0 end)),
       (coalesce(sum(a.totalMinutesWorked), 0) / 60.0)
    )
    from Attendance a
    where a.employee.userId = :employeeId
      and month(a.date) = :month
      and year(a.date) = :year
    group by a.employee.userId, a.employee.firstName, a.employee.lastName
""")
    MonthlyAttendanceReport findMonthlyReportByEmployee_UserIdAndMonthAndYear(
            @Param("employeeId") Long employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("""
    select new com.ds.app.dto.response.TeamAttendanceReportRow(
        e.userId,
        concat(e.firstName, ' ', e.lastName),
        :date,
        coalesce(a.status, com.ds.app.enums.AttendanceStatus.ABSENT),
        a.punchInTime,
        a.punchOutTime,
        a.totalMinutesWorked,
        coalesce(a.isLate, false)
    )
    from Employee e
    left join Attendance a
      on a.employee.userId = e.userId
     and a.date = :date
    where e.manager.userId = :managerId
    order by e.firstName asc, e.lastName asc
""")
    List<TeamAttendanceReportRow> findTeamAttendanceReportByManagerAndDate(
            @Param("managerId") Long managerId,
            @Param("date") LocalDate date
    );
}
