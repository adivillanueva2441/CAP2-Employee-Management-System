package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ReportServiceImpl implements IReportService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public BigDecimal getAverageSalary() {
        return employeeRepository.findAverageSalary();
    }

    @Override
    public double getAverageAge() {
        return employeeRepository.findAverageAge();
    }

    @Override
    public long getTotalEmployees() {
        return employeeRepository.count();
    }

    @Override
    public long getTotalDepartments() {
        return departmentRepository.count();
    }

    @Override
    public byte[] exportToCSV() {
        List<Employee> employees = employeeRepository.findAll();

        // Formats numbers for salary to have comma on appropriate digits
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // writes the summary
        writer.println("EMS SUMMARY REPORT");
        writer.println("Report Date," + LocalDate.now());
        writer.println("Export Time," + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        writer.println("Total Employees," + employeeRepository.count());
        writer.println("Total Departments," + departmentRepository.count());
        BigDecimal avgSalary = employeeRepository.findAverageSalary();
        writer.println("Average Salary," + (avgSalary != null ? "\"" + currencyFormat.format(avgSalary) + "\"" : "N/A"));

        Double avgAge = employeeRepository.findAverageAge();
        writer.println("Average Age," + (avgAge != null ? avgAge : "N/A"));
        writer.println();

        // write each employee
        writer.println("EMPLOYEE LIST");
        writer.println("Emp No.,Name,Department,Date of Birth,Age,Salary");

        employees.forEach(employee -> {
            int age = Period.between(employee.getDateOfBirth(), LocalDate.now()).getYears();
            writer.println(
                    employee.getEmployeeNumber() + "," +
                            employee.getName() + "," +
                            employee.getDepartment().getDepartmentName() + "," +
                            employee.getDateOfBirth() + "," +
                            age + "," +
                            "\"" + currencyFormat.format(employee.getSalary()) + "\""
            );
        });

        writer.flush();
        return out.toByteArray();
    }
}
