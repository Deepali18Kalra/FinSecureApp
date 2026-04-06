package com.ds.app.entity;

import com.ds.app.enums.Status;
import com.ds.app.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeTest {

    @Test
    void shouldCreateEmployeeUsingAllArgsConstructor() {

        Employee emp = new Employee();
        emp.setFirstName("Rahul");
        emp.setLastName("Verma");
        emp.setCurrentSalary(60000.0);
        emp.setStatus(Status.ACTIVE);
        emp.setEmail("rahul@test.com");
        emp.setEmployeeCode("EMP001");
        emp.setJoiningDate(LocalDate.of(2024, 1, 1));

        assertThat(emp.getFirstName()).isEqualTo("Rahul");
        assertThat(emp.getLastName()).isEqualTo("Verma");
        assertThat(emp.getCurrentSalary()).isEqualTo(60000.0);
        assertThat(emp.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(emp.getEmail()).isEqualTo("rahul@test.com");
        assertThat(emp.getEmployeeCode()).isEqualTo("EMP001");
        assertThat(emp.getJoiningDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void shouldReturnCompanyIdWhenCompanyIsSet() {

        Company company = new Company();
        company.setId(10L);

        Employee emp = new Employee();
        emp.setCompany(company);

        assertThat(emp.getCompanyId()).isEqualTo(10L);
    }

    @Test
    void shouldReturnNullCompanyIdWhenCompanyIsNull() {

        Employee emp = new Employee();

        assertThat(emp.getCompanyId()).isNull();
    }

    @Test
    void customConstructorShouldInitializeAppUserFields() {

        Employee emp = new Employee(
                "rahul.user",
                "secret",
                false,
                UserRole.EMPLOYEE,
                "Rahul",
                "Verma",
                "EMP002"
        );

        assertThat(emp.getUsername()).isEqualTo("rahul.user");
        assertThat(emp.getFirstName()).isEqualTo("Rahul");
        assertThat(emp.getLastName()).isEqualTo("Verma");
        assertThat(emp.getEmployeeCode()).isEqualTo("EMP002");
    }
}