package com.ds.app.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ds.app.dto.EscalationReportDTO;
import com.ds.app.entity.AssetEscalation;
import com.ds.app.entity.Employee;
import com.ds.app.enums.EscalationStatus;

@Repository
public interface AssetEscalationRepository extends JpaRepository<AssetEscalation, Long> {
	List<AssetEscalation> findByEmployee_UserId(Integer userId);
	List<AssetEscalation> findByStatus(EscalationStatus status);
	List<AssetEscalation> findByAsset_AssetId(Long assetId);
	boolean existsByEmployeeAndStatus(Employee employee, EscalationStatus status);
	
	
	@Query("""
		    SELECT new com.ds.app.dto.EscalationReportDTO(
		        e.asset.assetId,
		        e.asset.name,
		        e.createdAt,
		        e.reason,
		        e.status,
		        e.employee.userId,
		        e.employee.username
		    )
		    FROM AssetEscalation e
		    WHERE e.employee.userId = :userId
		""")
		List<EscalationReportDTO> findEscalationsForEmployee(@Param("userId") Long userId);
	
	
	
	Optional<AssetEscalation> findFirstByEmployeeAndStatusOrderByCreatedAtDesc(
			Employee employee, EscalationStatus status
			);
	

	@Query("""
		    SELECT new com.ds.app.dto.EscalationReportDTO(
		        a.assetId,
		        a.name,
		        e.createdAt,
		        e.reason,
		        e.status,
		        emp.userId,
		        emp.username
		    )
		    FROM AssetEscalation e
		    JOIN e.asset a
		    JOIN e.employee emp
		    WHERE DATE(e.createdAt) = :date
		""")
		List<EscalationReportDTO> findEscalationsOnDate(
		        @Param("date") LocalDate date
		);


	@Query("""
		    SELECT new com.ds.app.dto.EscalationReportDTO(
		        a.assetId,
		        a.name,
		        e.createdAt,
		        e.reason,
		        e.status,
		        emp.userId,
		        emp.username
		    )
		    FROM AssetEscalation e
		    JOIN e.asset a
		    JOIN e.employee emp
		    WHERE e.createdAt BETWEEN :start AND :end
		""")
		List<EscalationReportDTO> findEscalationsOnDate(
		        @Param("start") LocalDateTime start,
		        @Param("end") LocalDateTime end
		);

}
