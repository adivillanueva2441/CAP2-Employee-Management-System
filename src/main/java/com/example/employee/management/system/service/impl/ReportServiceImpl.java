package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ReportServiceImpl implements IReportService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public BigDecimal getAverageSalary() {
        return employeeRepository.findAverageSalary();
    }

    @Override
    public double getAverageAge() {
        return employeeRepository.findAverageAge();
    }
}
