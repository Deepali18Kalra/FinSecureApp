package com.ds.app.repository;
import com.ds.app.entity.EmployeeInvestment;
import com.ds.app.enums.ComplianceStatus;
import com.ds.app.enums.InvestmentType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeInvestmentRepository
        extends JpaRepository<EmployeeInvestment, Long> {

    // get all investments of one employee
    Page<EmployeeInvestment> findByEmployee_UserId(
        Long userId, Pageable pageable
    );

    // get all NON-COMPLIANT investments
    Page<EmployeeInvestment> findByComplianceStatus(
        ComplianceStatus status, Pageable pageable
    );

    // get investments by type
    Page<EmployeeInvestment> findByInvestmentType(
        InvestmentType type, Pageable pageable
    );

    // check if employee already declared same security
    Boolean existsByEmployee_UserIdAndSecurityName(
        Long userId, String securityName
    );

    // check if employee already declared same mutual fund
    Boolean existsByEmployee_UserIdAndMutualFund_MutualFundId(
        Long userId, Long mutualFundId
    );

    // ✅ CORRECT way to query by company
    Page<EmployeeInvestment> findByEmployee_Company_Id(
        Long companyId, Pageable pageable
    );
}
