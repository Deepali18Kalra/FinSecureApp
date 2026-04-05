package com.ds.app.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.entity.SalaryJob;
import com.ds.app.repository.EmployeeBankAccountRepository;
import com.ds.app.service.EmployeeAccountService;

import lombok.RequiredArgsConstructor;

@Component
public class ModifiedTodayScheduler {
	
	@Autowired
	EmployeeAccountService accountService;
	
	@Autowired
	EmployeeBankAccountRepository accountRepository;
	
	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Kolkata")
	public void resetModifiedToZero()
	{
		List<EmployeeBankAccount> accounts = accountRepository.findResetableEmployees();
		
		for (EmployeeBankAccount a : accounts) {
            try {
            	accountService.resetToZero(a.getEmpBankId());
            } catch (Exception e) {
                System.err.println("Job failed For : " + a.getAccountHolderName()
                        + " — " + e.getMessage());
            }
        }
	}
	
	
	
	

}
