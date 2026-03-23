package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.exceptions.DuplicateEntryException;
import com.example.employee.management.system.exceptions.ResourceNotFoundException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.IEmployeeService;
import com.example.employee.management.system.service.IMessageHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private IMessageHandlerService messageHandlerService;

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
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService
                        .get("employee.not.found", employeeId)));
        return new EmployeeDtoResponse(employee);
    }

    //Search by employee name
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> searchEmployeeName(String employeeName, Pageable pageable) {
        if (employeeName == null || employeeName.trim().isEmpty()) {
            throw new BadRequestException(messageHandlerService.get("employee.search.empty", employeeName));
        }
        return employeeRepository.findByNameContainingIgnoreCase(employeeName, pageable)
                .map(EmployeeDtoResponse::new);
    }

    //Filters Employee List by Age range
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> filterByAgeRange(Integer minAge, Integer maxAge, Pageable pageable){
        if (minAge < 18 || maxAge < 18 || minAge > 80 || maxAge > 80) {
            throw new BadRequestException(messageHandlerService.get("employee.age.invalid"));
        }
        if (minAge > maxAge) {
            throw new BadRequestException(messageHandlerService.get("employee.age.minimum.higher.than.maximum"));
        }
        return employeeRepository.findByAgeBetween(minAge, maxAge, pageable)
                .map(EmployeeDtoResponse::new);

    }

    //Filters Employee List by department
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> filterByDepartmentId(Long departmentId, Pageable pageable){
        if(!departmentRepository.existsById(departmentId)){
            throw new ResourceNotFoundException(messageHandlerService.get("department.not.found", departmentId));
        }

        return employeeRepository.findByDepartment_DepartmentId(departmentId, pageable)
                .map(EmployeeDtoResponse::new);

    }


    //Filters Employee List by department and age range
    @Transactional
    @Override
    public Page<EmployeeDtoResponse> filterByDepartmentAndAge (Long departmentId, Integer minAge, Integer maxAge,
                                                               Pageable pageable){
        if ((minAge != null && maxAge == null) || (minAge == null && maxAge != null)) {
            throw new BadRequestException(messageHandlerService.get("employee.provide.both.min.max.age"));
        }
        if(!departmentRepository.existsById(departmentId)){
            throw new ResourceNotFoundException(messageHandlerService.get("department.not.found", departmentId));
        }
        if (minAge < 18 || maxAge < 18 || minAge > 80 || maxAge > 80) {
            throw new BadRequestException(messageHandlerService.get("employee.age.invalid"));
        }
        if (minAge > maxAge) {
            throw new BadRequestException(messageHandlerService.get("employee.age.minimum.higher.than.maximum"));
        }

        return employeeRepository.findByDepartmentAndAgeBetween(departmentId, minAge, maxAge, pageable)
                .map(EmployeeDtoResponse::new);
    }

    //Adds new Employee
    @Transactional
    @Override
    public EmployeeDtoResponse addNewEmployee(EmployeeDtoRequest employeeDtoRequest){
        employeeDtoRequest.setName(normalizeName(employeeDtoRequest.getName()));

        Department department = departmentRepository.findById(employeeDtoRequest.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("department.not.found", employeeDtoRequest.getDepartmentId())));

        if (employeeRepository.existsByNameAndDateOfBirth(
                employeeDtoRequest.getName(),
                employeeDtoRequest.getDateOfBirth())) {
            throw new DuplicateEntryException(messageHandlerService.get("employee.already.exists"));
        }

        // validate age before saving
        validateAge(employeeDtoRequest.getDateOfBirth());
        // validate salary before saving
        validateSalary(employeeDtoRequest.getSalary());

        return createNewEmployee(employeeDtoRequest, department);
    }

    //Updates Employee by employeeId
    @Transactional
    @Override
    public String updateEmployeeDetails(Long employeeId, EmployeeDtoRequest employeeDtoRequest) {
        // normalize name before anything else
        employeeDtoRequest.setName(normalizeName(employeeDtoRequest.getName()));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageHandlerService.get("employee.not.found", employeeId)));

        Department department = departmentRepository.findById(employeeDtoRequest.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageHandlerService.get("department.not.found", employeeDtoRequest.getDepartmentId())));

        //Checks for a duplicate record in the database
        boolean isDuplicate = employeeRepository.existsByNameAndDateOfBirth(
                employeeDtoRequest.getName(),
                employeeDtoRequest.getDateOfBirth());

        //verifies that the employee being updated is the same person that is in the database
        boolean isSameEmployee = employee.getName().equals(employeeDtoRequest.getName())
                && employee.getDateOfBirth().equals(employeeDtoRequest.getDateOfBirth());

        // if there's a duplicate record, and it is not the same employee being updated, throw error as employee already exists
        if (isDuplicate && !isSameEmployee) {
            throw new DuplicateEntryException(messageHandlerService.get("employee.already.exists"));
        }

        validateAge(employeeDtoRequest.getDateOfBirth());
        validateSalary(employeeDtoRequest.getSalary());

        updateEmployee(employeeDtoRequest, employee, department);
        return messageHandlerService.get("employee.updated.success");
    }

    //Deletes Employee by employeeId
    @Transactional
    @Override
    public String deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException(messageHandlerService.get("employee.not.found", employeeId));
        }
        employeeRepository.deleteById(employeeId);
        return messageHandlerService.get("employee.deleted.success");
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

    //Formats names to have spaces between words
    private String normalizeName(String name) {
        if (name == null) return null;

        // trim leading and trailing spaces
        // then replace multiple spaces between words with a single space
        String normalized = name.trim().replaceAll("\\s+", " ");

        // check if name has at least two words (first and last name)
        if (!normalized.contains(" ")) {
            throw new BadRequestException(messageHandlerService.get("employee.name.invalid"));
        }

        return normalized;
    }

    //Validate that the date of birth of an employee results between 18-80 years old.
    private void validateAge(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18 || age > 80) {
            throw new BadRequestException(messageHandlerService.get("employee.age.invalid"));
        }
    }

    // Example Minimum Wage
    @Value("${employee.minimum.wage}")
    private BigDecimal minimumWage;

    private void validateSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(messageHandlerService.get("employee.salary.negative"));
        }
        if (salary.compareTo(minimumWage) < 0) {
            throw new BadRequestException(messageHandlerService.get("employee.salary.below.minimum"));
        }
    }


}
