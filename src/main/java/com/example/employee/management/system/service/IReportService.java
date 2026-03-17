package com.example.employee.management.system.service;

import java.math.BigDecimal;

public interface IReportService {

    BigDecimal getAverageSalary();
    double getAverageAge();
    long getTotalEmployees();
    long getTotalDepartments();
    byte[] exportToCSV();
}
