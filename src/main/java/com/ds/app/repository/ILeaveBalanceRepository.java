package com.ds.app.repository;

import com.ds.app.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    boolean existsByEmployeeUserIdAndYear(Long employeeId, Integer year);

    Optional<LeaveBalance> findByEmployeeUserIdAndYear(Long employeeId, Integer year);

    @Query("""
            select lb from LeaveBalance lb
            where lb.employee.manager.userId = :managerId
            and lb.year = :year
            order by lb.employee.firstName asc
            """)
    List<LeaveBalance> findByEmployeeManagerUserIdAndYear(
            @Param("managerId") Long managerId,
            @Param("year") Integer year);
}