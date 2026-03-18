package com.example.employee.management.system.controller;
import com.example.employee.management.system.controller.api.EmployeeApiController;
import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.service.IEmployeeService;
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
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class EmployeeApiControllerTest {

    @Mock
    private IEmployeeService employeeService;

    @InjectMocks
    private EmployeeApiController employeeApiController;

    private Employee employee;
    private Department department;
    private EmployeeDtoResponse employeeDtoResponse;
    private EmployeeDtoRequest employeeDtoRequest;
    private Pageable pageable;

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

        employeeDtoResponse = new EmployeeDtoResponse(employee);

        employeeDtoRequest = new EmployeeDtoRequest();
        employeeDtoRequest.setName("Juan dela Cruz");
        employeeDtoRequest.setDepartmentId(1L);
        employeeDtoRequest.setDateOfBirth(LocalDate.of(2000, 1, 1));
        employeeDtoRequest.setSalary(BigDecimal.valueOf(20000.00));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllEmployees_returns200WithPageOfEmployees() {
        Page<EmployeeDtoResponse> page = new PageImpl<>(List.of(employeeDtoResponse), pageable, 1);
        when(employeeService.getAllEmployees(pageable)).thenReturn(page);

        ResponseEntity<Page<EmployeeDtoResponse>> response = employeeApiController.getAllEmployees(pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getName()).isEqualTo(employee.getName());
        verify(employeeService, times(1)).getAllEmployees(pageable);
    }

    @Test
    void getAllEmployees_returns200WithEmptyPage_whenNoEmployeesExist() {
        Page<EmployeeDtoResponse> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(employeeService.getAllEmployees(pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<EmployeeDtoResponse>> response = employeeApiController.getAllEmployees(pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).isEmpty();
        verify(employeeService, times(1)).getAllEmployees(pageable);
    }

    @Test
    void getEmployeeById_returns200WithEmployee() {
        when(employeeService.getEmployeeById(1L)).thenReturn(employeeDtoResponse);

        ResponseEntity<EmployeeDtoResponse> response = employeeApiController.getEmployeeById(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getEmployeeId()).isEqualTo(employee.getEmployeeId());
        assertThat(response.getBody().getName()).isEqualTo(employee.getName());
        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    void searchEmployees_returns200WithPageOfEmployees() {
        Page<EmployeeDtoResponse> page = new PageImpl<>(List.of(employeeDtoResponse), pageable, 1);
        when(employeeService.searchEmployeeName("Juan", pageable)).thenReturn(page);

        ResponseEntity<Page<EmployeeDtoResponse>> response = employeeApiController.searchEmployees("Juan", pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getName()).isEqualTo(employee.getName());
        verify(employeeService, times(1)).searchEmployeeName("Juan", pageable);
    }

    @Test
    void searchEmployees_returns200WithEmptyPage_whenNoEmployeesMatch() {
        Page<EmployeeDtoResponse> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(employeeService.searchEmployeeName("Unknown", pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<EmployeeDtoResponse>> response = employeeApiController.searchEmployees("Unknown", pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).isEmpty();
        verify(employeeService, times(1)).searchEmployeeName("Unknown", pageable);
    }

    @Test
    void filter_returns200WithAllEmployees_whenNoFilters() {
        Page<EmployeeDtoResponse> page = new PageImpl<>(List.of(employeeDtoResponse), pageable, 1);
        when(employeeService.getAllEmployees(pageable)).thenReturn(page);

        ResponseEntity<Page<EmployeeDtoResponse>> response =
                employeeApiController.filterByDepartmentAndAge(null, null, null, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(employeeService, times(1)).getAllEmployees(pageable);
    }

    @Test
    void filter_returns200WithFilteredEmployees_whenDepartmentIdProvided() {
        Page<EmployeeDtoResponse> page = new PageImpl<>(List.of(employeeDtoResponse), pageable, 1);
        when(employeeService.filterByDepartmentId(1L, pageable)).thenReturn(page);

        ResponseEntity<Page<EmployeeDtoResponse>> response =
                employeeApiController.filterByDepartmentAndAge(1L, null, null, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(employeeService, times(1)).filterByDepartmentId(1L, pageable);
    }

    @Test
    void filter_returns200WithFilteredEmployees_whenAgeRangeProvided() {
        Page<EmployeeDtoResponse> page = new PageImpl<>(List.of(employeeDtoResponse), pageable, 1);
        when(employeeService.filterByAgeRange(20, 30, pageable)).thenReturn(page);

        ResponseEntity<Page<EmployeeDtoResponse>> response =
                employeeApiController.filterByDepartmentAndAge(null, 20, 30, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(employeeService, times(1)).filterByAgeRange(20, 30, pageable);
    }

    @Test
    void filter_returns200WithFilteredEmployees_whenBothFiltersProvided() {
        Page<EmployeeDtoResponse> page = new PageImpl<>(List.of(employeeDtoResponse), pageable, 1);
        when(employeeService.filterByDepartmentAndAge(1L, 20, 30, pageable)).thenReturn(page);

        ResponseEntity<Page<EmployeeDtoResponse>> response =
                employeeApiController.filterByDepartmentAndAge(1L, 20, 30, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        verify(employeeService, times(1)).filterByDepartmentAndAge(1L, 20, 30, pageable);
    }

    @Test
    void filter_throwsBadRequestException_whenOnlyMinAgeProvided() {
        assertThatThrownBy(() -> employeeApiController.filterByDepartmentAndAge(null, 20, null, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Both minAge and maxAge must be provided together");
    }

    @Test
    void filter_throwsBadRequestException_whenOnlyMaxAgeProvided() {
        assertThatThrownBy(() -> employeeApiController.filterByDepartmentAndAge(null, null, 30, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Both minAge and maxAge must be provided together");
    }

    @Test
    void addNewEmployee_returns201WithCreatedEmployee() {
        when(employeeService.addNewEmployee(employeeDtoRequest)).thenReturn(employeeDtoResponse);

        ResponseEntity<EmployeeDtoResponse> response = employeeApiController.addNewEmployee(employeeDtoRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().getName()).isEqualTo(employee.getName());
        assertThat(response.getBody().getSalary()).isEqualTo(employee.getSalary());
        verify(employeeService, times(1)).addNewEmployee(employeeDtoRequest);
    }

    @Test
    void updateEmployee_returns204() {
        doNothing().when(employeeService).updateEmployeeDetails(1L, employeeDtoRequest);

        ResponseEntity<Void> response = employeeApiController.updateEmployee(1L, employeeDtoRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(employeeService, times(1)).updateEmployeeDetails(1L, employeeDtoRequest);
    }

    @Test
    void deleteEmployee_returns204() {
        doNothing().when(employeeService).deleteEmployee(1L);

        ResponseEntity<Void> response = employeeApiController.deleteEmployee(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(employeeService, times(1)).deleteEmployee(1L);
    }
}
