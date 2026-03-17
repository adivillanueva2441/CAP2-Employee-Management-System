package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.exceptions.ResourceNotFoundException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;


@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    //Retrieve all Employee Data
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> getAllEmployees(Pageable pageable){
        return employeeRepository.findAll(pageable)
                .map(EmployeeDtoResponse::new);
    }

    //Retrieve specific employee details
    @Transactional
    @Override
    public EmployeeDtoResponse getEmployeeById(Long employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return new EmployeeDtoResponse(employee);
    }

    //Search by employee name
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> searchEmployeeName(String employeeName, Pageable pageable) {
        if (employeeName == null || employeeName.trim().isEmpty()) {
            throw new BadRequestException("Search name cannot be empty");
        }
        return employeeRepository.findByNameContainingIgnoreCase(employeeName, pageable)
                .map(EmployeeDtoResponse::new);
    }

    //Filters Employee List by Age range
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> filterByAgeRange(int minAge, int maxAge, Pageable pageable){
        if (minAge < 18 || maxAge < 18 || minAge > 80 || maxAge > 80) {
            throw new BadRequestException("Age must be between 18 and 80");
        }
        if (minAge > maxAge) {
            throw new BadRequestException("Minimum age cannot be greater than maximum age");
        }
        return employeeRepository.findByAgeBetween(minAge, maxAge, pageable)
                .map(EmployeeDtoResponse::new);

    }

    //Filters Employee List by department
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> filterByDepartmentId(Long departmentId, Pageable pageable){
        if(!departmentRepository.existsById(departmentId)){
            throw new ResourceNotFoundException("Department not found");
        }

        return employeeRepository.findByDepartment_DepartmentId(departmentId, pageable)
                .map(EmployeeDtoResponse::new);

    }


    //Filters Employee List by department and age range
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> filterByDepartmentAndAge (Long departmentId, int minAge, int maxAge,
                                                               Pageable pageable){
        if(!departmentRepository.existsById(departmentId)){
            throw new ResourceNotFoundException("Department not found");
        }
        if (minAge < 18 || maxAge < 18 || minAge > 80 || maxAge > 80) {
            throw new BadRequestException("Age must be between 18 and 80");
        }
        if (minAge > maxAge) {
            throw new BadRequestException("Minimum age cannot be greater than maximum age");
        }

        return employeeRepository.findByDepartmentAndAgeBetween(departmentId, minAge, maxAge, pageable)
                .map(EmployeeDtoResponse::new);
    }

    //Adds new Employee
    @Transactional
    @Override
    public EmployeeDtoResponse addNewEmployee(EmployeeDtoRequest employeeDtoRequest){
        Department department = departmentRepository.findById(employeeDtoRequest.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // validate age before saving
        validateAge(employeeDtoRequest.getDateOfBirth());
        // validate salary before saving
        validateSalary(employeeDtoRequest.getSalary());

        return createNewEmployee(employeeDtoRequest, department);
    }

    //Updates Employee by employeeId
    @Transactional
    @Override
    public void updateEmployeeDetails(Long employeeId, EmployeeDtoRequest employeeDtoRequest){
        Employee employee =  employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Department department = departmentRepository.findById(employeeDtoRequest.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // validate age before saving
        validateAge(employeeDtoRequest.getDateOfBirth());

        //validate salary before saving
        validateSalary(employeeDtoRequest.getSalary());

        updateEmployee(employeeDtoRequest, employee, department);
    }

    //Deletes Employee by employeeId
    @Transactional
    @Override
    public void deleteEmployee(Long employeeId){
        if(!employeeRepository.existsById(employeeId)){
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        employeeRepository.deleteById(employeeId);
    }

    //Update Existing Employee
    private void updateEmployee(EmployeeDtoRequest employeeDtoRequest, Employee employee, Department department) {
        employee.setName(employeeDtoRequest.getName());
        employee.setDepartment(department);
        employee.setSalary(employeeDtoRequest.getSalary());
        employee.setDateOfBirth(employeeDtoRequest.getDateOfBirth());

        employeeRepository.save(employee);
    }

    //Creation of New Employee
    private EmployeeDtoResponse createNewEmployee(EmployeeDtoRequest employeeDtoRequest, Department department) {
        Employee employee = new Employee();
        employee.setName(employeeDtoRequest.getName());
        employee.setDepartment(department);
        employee.setSalary(employeeDtoRequest.getSalary());
        employee.setDateOfBirth(employeeDtoRequest.getDateOfBirth());
        employee.setEmployeeNumber("EMP-TEMP"); // temporary placeholder

        Employee savedEmployee = employeeRepository.save(employee);

        //Generates Employee number based on "EMP- + 4 digit string derived from employee id"
        savedEmployee.setEmployeeNumber("EMP-" + String.format("%04d", savedEmployee.getEmployeeId()));
        employeeRepository.save(savedEmployee);

        return new EmployeeDtoResponse(savedEmployee);
    }

    //Validate that the date of birth of an employee results between 18-80 years old.
    private void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18 || age > 80) {
            throw new BadRequestException("Employee age must be between 18 and 80 years old");
        }
    }

    // Example Minimum Wage
    private static final BigDecimal MINIMUM_WAGE = BigDecimal.valueOf(19000.00);

    private void validateSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Salary cannot be negative");
        }
        if (salary.compareTo(MINIMUM_WAGE) < 0) {
            throw new BadRequestException("Salary cannot be below the minimum wage of ₱19,000.00");
        }
    }


}
