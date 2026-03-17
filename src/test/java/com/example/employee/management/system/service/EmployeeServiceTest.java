package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.exceptions.ResourceNotFoundException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Department department;
    private Employee employee;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {

        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Department");
        department.setEmployees(new ArrayList<>());

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeNumber("EMP-0001");
        employee.setName("Employee 1");
        employee.setDateOfBirth(LocalDate.now());
        employee.setSalary(BigDecimal.valueOf(67000.00));
        employee.setDepartment(department);

        pageable = PageRequest.of(0, 1);
    }

    @Test
    void getAllEmployees_returnListOfBooks() {

        Page<Employee> employeePage = new PageImpl<>(List.of(employee),pageable,1);

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        Page<EmployeeDtoResponse> response = employeeService.getAllEmployees(pageable);

        verify(employeeRepository, times(1)).findAll(pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getEmployeeId()).isEqualTo(employee.getEmployeeId());
        assertThat(response.getContent().get(0).getName()).isEqualTo(employee.getName());
        assertThat(response.getContent().get(0).getSalary()).isEqualTo(employee.getSalary());
        assertThat(response.getContent().get(0).getDepartment()).isEqualTo(department.getDepartmentName());
    }

    @Test
    void getAllEmployees_returnsEmptyPageWhenNoEmployees() {

        Page<Employee> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(employeeRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<EmployeeDtoResponse> response = employeeService.getAllEmployees(pageable);

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void getEmployeeById_returnsEmployee() {
        employee.setEmployeeId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDtoResponse response = employeeService.getEmployeeById(1L);

        verify(employeeRepository, times(1)).findById(1L);

        assertThat(response.getEmployeeId()).isEqualTo(employee.getEmployeeId());
        assertThat(response.getEmployeeNumber()).isEqualTo(employee.getEmployeeNumber());
        assertThat(response.getName()).isEqualTo(employee.getName());
        assertThat(response.getSalary()).isEqualTo(employee.getSalary());
        assertThat(response.getDepartment()).isEqualTo(department.getDepartmentName());
    }

    @Test
    void getEmployeeById_throwsResourceNotFoundException_whenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void searchEmployeeName_returnPageOfEmployees(){
        Page<Employee> employeePage = new PageImpl<>(List.of(employee),pageable,1);

        when(employeeRepository.findByNameContainingIgnoreCase("Employee 1", pageable)).thenReturn(employeePage);

        Page<EmployeeDtoResponse> response = employeeService.searchEmployeeName("Employee 1", pageable);

        verify(employeeRepository, times(1)).findByNameContainingIgnoreCase("Employee 1", pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getEmployeeId()).isEqualTo(employee.getEmployeeId());
        assertThat(response.getContent().get(0).getName()).isEqualTo(employee.getName());
        assertThat(response.getContent().get(0).getSalary()).isEqualTo(employee.getSalary());
        assertThat(response.getContent().get(0).getDepartment()).isEqualTo(department.getDepartmentName());
        assertThat(response.getContent().get(0).getEmployeeNumber()).isEqualTo(employee.getEmployeeNumber());
    }

    @Test
    void searchEmployeeName_throwsResourceNotFoundException_whenEmployeeNotFound() {
        assertThatThrownBy(() -> employeeService.searchEmployeeName("", pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Search name cannot be empty");

        verify(employeeRepository, never()).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void filterByAgeRange_returnPageOfEmployees(){
        Page<Employee> employeePage = new PageImpl<>(List.of(employee),pageable,1);

        when(employeeRepository.findByAgeBetween(20, 40, pageable)).thenReturn(employeePage);

        Page<EmployeeDtoResponse> response = employeeService.filterByAgeRange(20,40,pageable);

        verify(employeeRepository, times(1)).findByAgeBetween(20, 40, pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getEmployeeId()).isEqualTo(employee.getEmployeeId());
        assertThat(response.getContent().get(0).getEmployeeNumber()).isEqualTo(employee.getEmployeeNumber());
        assertThat(response.getContent().get(0).getName()).isEqualTo(employee.getName());
        assertThat(response.getContent().get(0).getSalary()).isEqualTo(employee.getSalary());
        assertThat(response.getContent().get(0).getDepartment()).isEqualTo(department.getDepartmentName());
    }








    @Test
    void filterByDepartmentAndAge_returnsPageOfEmployees() {

        Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);

        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.findByDepartmentAndAgeBetween(1L, 20, 30, pageable))
                .thenReturn(employeePage);

        Page<EmployeeDtoResponse> response = employeeService.filterByDepartmentAndAge(1L, 20, 30, pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getEmployeeId()).isEqualTo(employee.getEmployeeId());
        assertThat(response.getContent().get(0).getName()).isEqualTo(employee.getName());
        verify(departmentRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).findByDepartmentAndAgeBetween(1L, 20, 30, pageable);
    }





}
