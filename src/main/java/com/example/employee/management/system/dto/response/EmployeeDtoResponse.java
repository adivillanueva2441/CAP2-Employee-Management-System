package com.example.employee.management.system.dto.response;

import com.example.employee.management.system.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class EmployeeDtoResponse {

    private Long employeeId;
    private String employeeNumber;
    private String name;
    private String department;
    private LocalDate dateOfBirth;
    private BigDecimal salary;
    private int age;

        public EmployeeDtoResponse(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.employeeNumber = employee.getEmployeeNumber();
        this.name = employee.getName();
        this.department = employee.getDepartment().getDepartmentName();
        this.dateOfBirth = employee.getDateOfBirth();
        this.salary = employee.getSalary();
        this.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
