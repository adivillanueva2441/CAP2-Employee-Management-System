package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
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
    void filterByAgeRange_throwsBadRequestException_whenMinAgeBelowMinimum() {
        assertThatThrownBy(() -> employeeService.filterByAgeRange(17, 40, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByAgeBetween(anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByAgeRange_throwsBadRequestException_whenMaxAgeBelowMinimum() {
        assertThatThrownBy(() -> employeeService.filterByAgeRange(18, 17, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByAgeBetween(anyInt(), anyInt(), any(Pageable.class));

    }

    @Test
    void filterByAgeRange_throwsBadRequestException_whenMinAgeAboveMaximum() {
        assertThatThrownBy(() -> employeeService.filterByAgeRange(81, 40, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByAgeBetween(anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByAgeRange_throwsBadRequestException_whenMaxAgeAboveMaximum() {
        assertThatThrownBy(() -> employeeService.filterByAgeRange(20, 81, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByAgeBetween(anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByAgeRange_throwsBadRequestException_whenMinAgeGreaterThanMaxAge() {
        assertThatThrownBy(() -> employeeService.filterByAgeRange(40, 20, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Minimum age cannot be greater than maximum age");

        verify(employeeRepository, never()).findByAgeBetween(anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByAgeRange_returnsEmptyPage_whenNoEmployeesMatchFilter() {
        Page<Employee> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(employeeRepository.findByAgeBetween(20, 40, pageable)).thenReturn(emptyPage);

        Page<EmployeeDtoResponse> response = employeeService.filterByAgeRange(20, 40, pageable);

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
        verify(employeeRepository, times(1)).findByAgeBetween(20, 40, pageable);
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

    @Test
    void filterByDepartmentAndAge_throwsResourceNotFoundException_whenDepartmentNotFound() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.filterByDepartmentAndAge(1L, 20, 30, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department not found");

        verify(employeeRepository, never()).findByDepartmentAndAgeBetween(any(), anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByDepartmentAndAge_throwsBadRequestException_whenMinAgeBelowMinimum() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.filterByDepartmentAndAge(1L, 17, 30, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByDepartmentAndAgeBetween(any(), anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByDepartmentAndAge_throwsBadRequestException_whenMaxAgeBelowMinimum() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.filterByDepartmentAndAge(1L, 18, 17, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByDepartmentAndAgeBetween(any(), anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByDepartmentAndAge_throwsBadRequestException_whenMinAgeAboveMaximum() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.filterByDepartmentAndAge(1L, 81, 30, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByDepartmentAndAgeBetween(any(), anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByDepartmentAndAge_throwsBadRequestException_whenMaxAgeAboveMaximum() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.filterByDepartmentAndAge(1L, 20, 81, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Age must be between 18 and 80");

        verify(employeeRepository, never()).findByDepartmentAndAgeBetween(any(), anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    void filterByDepartmentAndAge_throwsBadRequestException_whenMinAgeGreaterThanMaxAge() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.filterByDepartmentAndAge(1L, 40, 20, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Minimum age cannot be greater than maximum age");

        verify(employeeRepository, never()).findByDepartmentAndAgeBetween(any(), anyInt(), anyInt(), any(Pageable.class));
    }


    @Test
    void filterByDepartmentAndAge_returnsEmptyPage_whenNoEmployeesMatchFilter() {
        Page<Employee> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.findByDepartmentAndAgeBetween(1L, 20, 30, pageable)).thenReturn(emptyPage);

        Page<EmployeeDtoResponse> response = employeeService.filterByDepartmentAndAge(1L, 20, 30, pageable);

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
        verify(departmentRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).findByDepartmentAndAgeBetween(1L, 20, 30, pageable);
    }

    @Test
    void addNewEmployee_returnsEmployeeDtoResponse() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Employee 1");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1)); // age 25 — valid
        request.setSalary(BigDecimal.valueOf(20000.00));   // above minimum wage — valid

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDtoResponse response = employeeService.addNewEmployee(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(employee.getName());
        assertThat(response.getSalary()).isEqualTo(employee.getSalary());
        assertThat(response.getDateOfBirth()).isEqualTo(employee.getDateOfBirth());
        assertThat(response.getDepartment()).isEqualTo(employee.getDepartment().getDepartmentName());
        assertThat(response.getSalary()).isEqualTo(employee.getSalary());
        assertThat(response.getDateOfBirth()).isEqualTo(employee.getDateOfBirth());

        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, times(2)).save(any(Employee.class)); // saves twice for employee number
    }

    @Test
    void addNewEmployee_throwsResourceNotFoundException_whenDepartmentNotFound() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Employee 1");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.addNewEmployee(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department not found");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void addNewEmployee_throwsBadRequestException_whenAgeBelowMinimum() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Employee 1");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.now().minusYears(17)); // 17 years old — invalid
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.addNewEmployee(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Employee age must be between 18 and 80 years old");

        verify(employeeRepository, never()).save(any(Employee.class));
    }


    @Test
    void addNewEmployee_throwsBadRequestException_whenAgeAboveMaximum() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Employee 1");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.now().minusYears(81)); // 81 years old — invalid
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.addNewEmployee(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Employee age must be between 18 and 80 years old");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void addNewEmployee_throwsBadRequestException_whenSalaryIsNegative() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Employee 1");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(-1000.00)); // negative — invalid

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.addNewEmployee(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Salary cannot be negative");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void addNewEmployee_throwsBadRequestException_whenSalaryBelowMinimumWage() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Employee 1");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(18000.00)); // below minimum wage — invalid

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.addNewEmployee(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Salary cannot be below the minimum wage of ₱19,000.00");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_updatesEmployeeSuccessfully() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1)); // age 25 — valid
        request.setSalary(BigDecimal.valueOf(20000.00));   // above minimum wage — valid

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.updateEmployeeDetails(1L, request);

        verify(employeeRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_throwsResourceNotFoundException_whenEmployeeNotFound() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployeeDetails(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_throwsResourceNotFoundException_whenDepartmentNotFound() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployeeDetails(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department not found");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_throwsBadRequestException_whenAgeBelowMinimum() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.now().minusYears(17)); // 17 years old — invalid
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.updateEmployeeDetails(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Employee age must be between 18 and 80 years old");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_throwsBadRequestException_whenAgeAboveMaximum() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.now().minusYears(81)); // 81 years old — invalid
        request.setSalary(BigDecimal.valueOf(20000.00));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.updateEmployeeDetails(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Employee age must be between 18 and 80 years old");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_throwsBadRequestException_whenSalaryIsNegative() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(-1000.00)); // negative — invalid

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.updateEmployeeDetails(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Salary cannot be negative");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeDetails_throwsBadRequestException_whenSalaryBelowMinimumWage() {
        EmployeeDtoRequest request = new EmployeeDtoRequest();
        request.setName("Updated Employee");
        request.setDepartmentId(1L);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setSalary(BigDecimal.valueOf(18000.00)); // below minimum wage — invalid

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> employeeService.updateEmployeeDetails(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Salary cannot be below the minimum wage of ₱19,000.00");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_deletesEmployeeSuccessfully() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployee_throwsResourceNotFoundException_whenEmployeeNotFound() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository, never()).deleteById(any());
    }


}
