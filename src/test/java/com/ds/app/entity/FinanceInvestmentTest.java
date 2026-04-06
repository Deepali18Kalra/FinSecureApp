package com.ds.app.entity;

import com.ds.app.enums.FundStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class FinanceInvestmentTest {

    @Test
    void builderShouldCreateValidFinanceInvestment() {

        LocalDateTime now = LocalDateTime.now();

        FinanceInvestment investment = FinanceInvestment.builder()
                .mutualFundId(1L)
                .fundName("ICICI Bluechip Fund")
                .fundCode("ICICI_BC")
                .category("Equity Large Cap")
                .status(FundStatus.WHITELISTED)
                .addedBy(99L)
                .createdAt(now)
                .updatedAt(now)
                .employeeInvestments(new ArrayList<>())
                .build();

        assertThat(investment.getMutualFundId()).isEqualTo(1L);
        assertThat(investment.getFundName()).isEqualTo("ICICI Bluechip Fund");
        assertThat(investment.getFundCode()).isEqualTo("ICICI_BC");
        assertThat(investment.getCategory()).isEqualTo("Equity Large Cap");
        assertThat(investment.getStatus()).isEqualTo(FundStatus.WHITELISTED);
        assertThat(investment.getAddedBy()).isEqualTo(99L);
        assertThat(investment.getCreatedAt()).isEqualTo(now);
        assertThat(investment.getUpdatedAt()).isEqualTo(now);
        assertThat(investment.getEmployeeInvestments()).isEmpty();
    }

    @Test
    void settersAndGettersShouldWorkCorrectly() {

        FinanceInvestment investment = new FinanceInvestment();

        investment.setMutualFundId(2L);
        investment.setFundName("Axis ELSS Fund");
        investment.setFundCode("AXIS_ELSS");
        investment.setCategory("ELSS");
        investment.setStatus(FundStatus.BLACKLISTED);
        investment.setAddedBy(101L);

        assertThat(investment.getMutualFundId()).isEqualTo(2L);
        assertThat(investment.getFundName()).isEqualTo("Axis ELSS Fund");
        assertThat(investment.getFundCode()).isEqualTo("AXIS_ELSS");
        assertThat(investment.getCategory()).isEqualTo("ELSS");
        assertThat(investment.getStatus()).isEqualTo(FundStatus.BLACKLISTED);
        assertThat(investment.getAddedBy()).isEqualTo(101L);
    }

    @Test
    void employeeInvestmentsListShouldBeMutable() {

        FinanceInvestment investment = new FinanceInvestment();
        investment.setEmployeeInvestments(new ArrayList<>());

        EmployeeInvestment empInvestment = new EmployeeInvestment();
        investment.getEmployeeInvestments().add(empInvestment);

        assertThat(investment.getEmployeeInvestments()).hasSize(1);
        assertThat(investment.getEmployeeInvestments().get(0))
                .isEqualTo(empInvestment);
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {

        FinanceInvestment investment = new FinanceInvestment();

        assertThat(investment.getMutualFundId()).isNull();
        assertThat(investment.getFundName()).isNull();
        assertThat(investment.getFundCode()).isNull();
        assertThat(investment.getCategory()).isNull();
        assertThat(investment.getStatus()).isNull();
        assertThat(investment.getAddedBy()).isNull();
        assertThat(investment.getCreatedAt()).isNull();
        assertThat(investment.getUpdatedAt()).isNull();
        assertThat(investment.getEmployeeInvestments()).isNull();
    }
}