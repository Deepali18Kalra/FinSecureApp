package com.ds.app.repository;
import com.ds.app.entity.FinanceInvestment;
import com.ds.app.enums.FundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinanceInvestmentRepository extends JpaRepository<FinanceInvestment, Long> {

    boolean existsByFundCode(String fundCode);

    boolean existsByFundName(String fundName);

    Optional<FinanceInvestment> findByFundCode(String fundCode);

    Page<FinanceInvestment> findAll(Pageable pageable);

    Page<FinanceInvestment> findByStatus(FundStatus status, Pageable pageable);

    Page<FinanceInvestment> findByCategory(String category, Pageable pageable);

    Page<FinanceInvestment> findByStatusAndCategory(FundStatus status, String category, Pageable pageable);
}
