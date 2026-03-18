package com.example.employee.management.system.service;

import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {


    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Department department;
    private Employee employee;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Engineering");
        department.setEmployees(new ArrayList<>());

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeNumber("EMP-0001");
        employee.setName("Juan dela Cruz");
        employee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        employee.setSalary(BigDecimal.valueOf(20000.00));
        employee.setDepartment(department);
    }


    @Test
    void getAverageSalary_returnsAverageSalary() {
        when(employeeRepository.findAverageSalary()).thenReturn(BigDecimal.valueOf(20000.00));

        BigDecimal response = reportService.getAverageSalary();

        assertThat(response).isEqualTo(BigDecimal.valueOf(20000.00));
        verify(employeeRepository, times(1)).findAverageSalary();
    }

    @Test
    void getAverageSalary_returnsNull_whenNoEmployeesExist() {
        when(employeeRepository.findAverageSalary()).thenReturn(null);

        BigDecimal response = reportService.getAverageSalary();

        assertThat(response).isNull();
        verify(employeeRepository, times(1)).findAverageSalary();
    }

    @Test
    void getAverageAge_returnsAverageAge() {
        when(employeeRepository.findAverageAge()).thenReturn(34.5);

        double response = reportService.getAverageAge();

        assertThat(response).isEqualTo(34.5);
        verify(employeeRepository, times(1)).findAverageAge();
    }

    @Test
    void getTotalEmployees_returnsTotalCount() {
        when(employeeRepository.count()).thenReturn(100L);

        long response = reportService.getTotalEmployees();

        assertThat(response).isEqualTo(100L);
        verify(employeeRepository, times(1)).count();
    }

    @Test
    void getTotalEmployees_returnsZero_whenNoEmployeesExist() {
        when(employeeRepository.count()).thenReturn(0L);

        long response = reportService.getTotalEmployees();

        assertThat(response).isEqualTo(0L);
        verify(employeeRepository, times(1)).count();
    }

    @Test
    void getTotalDepartments_returnsTotalCount() {
        when(departmentRepository.count()).thenReturn(5L);

        long response = reportService.getTotalDepartments();

        assertThat(response).isEqualTo(5L);
        verify(departmentRepository, times(1)).count();
    }

    @Test
    void getTotalDepartments_returnsZero_whenNoDepartmentsExist() {
        when(departmentRepository.count()).thenReturn(0L);

        long response = reportService.getTotalDepartments();

        assertThat(response).isEqualTo(0L);
        verify(departmentRepository, times(1)).count();
    }

    @Test
    void exportToCSV_returnsByteArray() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeRepository.count()).thenReturn(1L);
        when(departmentRepository.count()).thenReturn(1L);
        when(employeeRepository.findAverageSalary()).thenReturn(BigDecimal.valueOf(20000.00));
        when(employeeRepository.findAverageAge()).thenReturn(25.0);

        byte[] result = reportService.exportToCSV();

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void exportToCSV_containsCorrectHeaders() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeRepository.count()).thenReturn(1L);
        when(departmentRepository.count()).thenReturn(1L);
        when(employeeRepository.findAverageSalary()).thenReturn(BigDecimal.valueOf(20000.00));
        when(employeeRepository.findAverageAge()).thenReturn(25.0);

        byte[] result = reportService.exportToCSV();
        String csv = new String(result);

        assertThat(csv).contains("EMS SUMMARY REPORT");
        assertThat(csv).contains("Total Employees");
        assertThat(csv).contains("Total Departments");
        assertThat(csv).contains("Average Salary");
        assertThat(csv).contains("Average Age");
        assertThat(csv).contains("EMPLOYEE LIST");
        assertThat(csv).contains("Emp No.,Name,Department,Date of Birth,Age,Salary");
    }

    @Test
    void exportToCSV_containsEmployeeData() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeRepository.count()).thenReturn(1L);
        when(departmentRepository.count()).thenReturn(1L);
        when(employeeRepository.findAverageSalary()).thenReturn(BigDecimal.valueOf(20000.00));
        when(employeeRepository.findAverageAge()).thenReturn(25.0);

        byte[] result = reportService.exportToCSV();
        String csv = new String(result);

        assertThat(csv).contains(employee.getEmployeeNumber());
        assertThat(csv).contains(employee.getName());
        assertThat(csv).contains(department.getDepartmentName());
        assertThat(csv).contains(employee.getDateOfBirth().toString());
    }

    @Test
    void exportToCSV_returnsEmptyEmployeeList_whenNoEmployeesExist() {
        when(employeeRepository.findAll()).thenReturn(List.of());
        when(employeeRepository.count()).thenReturn(0L);
        when(departmentRepository.count()).thenReturn(0L);
        when(employeeRepository.findAverageSalary()).thenReturn(null);
        when(employeeRepository.findAverageAge()).thenReturn(0.0);

        byte[] result = reportService.exportToCSV();
        String csv = new String(result);

        assertThat(result).isNotNull();
        assertThat(csv).contains("EMS SUMMARY REPORT");
        assertThat(csv).contains("EMPLOYEE LIST");
        assertThat(csv).doesNotContain("EMP-");
    }
}

