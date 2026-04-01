package com.ds.app.repository;
import com.ds.app.entity.FinanceBankAccount;
import com.ds.app.enums.BankStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceBankRepository extends JpaRepository<FinanceBankAccount, Long> {

    boolean existsBybankCode(String bankCode);

    boolean existsByBankName(String bankName);

    Optional<FinanceBankAccount> findBybankCode(String ifscCode);

    List<FinanceBankAccount> findByStatus(BankStatus status);

    Page<FinanceBankAccount> findAll(Pageable pageable);

    Page<FinanceBankAccount> findByStatus(BankStatus status, Pageable pageable);
}
