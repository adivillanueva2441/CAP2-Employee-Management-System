package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.repository.EmployeeRepository;
import com.example.employee.management.system.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<EmployeeDtoResponse> getAllEmployees(){
        return employeeRepository.findAll()
                .stream().map(EmployeeDtoResponse::new).toList();
    }

    @Override
    public EmployeeDtoResponse getEmployeeById(Long employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return new EmployeeDtoResponse(employee);
    }


    @Override
    public List<EmployeeDtoResponse> filterByAgeRange(int minAge, int maxAge){
        if (minAge < 0 || maxAge < 0) {
            throw new RuntimeException("Age cannot be negative");
        }
        if (minAge > maxAge) {
            throw new RuntimeException("Minimum age cannot be greater than maximum age");
        }

        return employeeRepository.findByAgeBetween(minAge, maxAge)
                .stream().map(EmployeeDtoResponse::new).toList();

    }

    @Override
    public List<EmployeeDtoResponse> filterByDepartmentId(Long departmentId){
        if(!departmentRepository.existsById(departmentId)){
            throw new RuntimeException("Department not found");
        }
        return employeeRepository.findByDepartment_DepartmentId(departmentId)
                .stream().map(EmployeeDtoResponse::new).toList();

    }


    @Override
    public List<EmployeeDtoResponse> filterByDepartmentAndAge (Long departmentId, int minAge, int maxAge){
        if(!departmentRepository.existsById(departmentId)){
            throw new RuntimeException("Department not found");
        }
        if (minAge < 0 || maxAge < 0) {
            throw new RuntimeException("Age cannot be negative");
        }
        if (minAge > maxAge) {
            throw new RuntimeException("Minimum age cannot be greater than maximum age");
        }
        return employeeRepository.findByDepartmentAndAgeBetween(departmentId,minAge,maxAge)
                .stream().map(EmployeeDtoResponse::new).toList();
    }

    @Transactional
    @Override
    public EmployeeDtoResponse addNewEmployee(EmployeeDtoRequest employeeDtoRequest){
        Department department = departmentRepository.findById(employeeDtoRequest.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return createNewEmployee(employeeDtoRequest, department);
    }

    @Transactional
    @Override
    public void updateEmployeeDetails(Long employeeId, EmployeeDtoRequest employeeDtoRequest){
        Employee employee =  employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Department department = departmentRepository.findById(employeeDtoRequest.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        updateEmployee(employeeDtoRequest, employee, department);
    }

    @Override
    public void deleteEmployee(Long employeeId){
        if(!employeeRepository.existsById(employeeId)){
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }
        employeeRepository.deleteById(employeeId);
    }

    private void updateEmployee(EmployeeDtoRequest employeeDtoRequest, Employee employee, Department department) {
        employee.setName(employeeDtoRequest.getName());
        employee.setDepartment(department);
        employee.setSalary(employeeDtoRequest.getSalary());
        employee.setDateOfBirth(employeeDtoRequest.getDateOfBirth());

        employeeRepository.save(employee);
    }

    private EmployeeDtoResponse createNewEmployee(EmployeeDtoRequest employeeDtoRequest, Department department) {
        Employee employee = new Employee();
        employee.setName(employeeDtoRequest.getName());
        employee.setDepartment(department);
        employee.setSalary(employeeDtoRequest.getSalary());
        employee.setDateOfBirth(employeeDtoRequest.getDateOfBirth());
        employee.setEmployeeNumber("EMP-TEMP"); // temporary placeholder

        Employee savedEmployee = employeeRepository.save(employee);
        savedEmployee.setEmployeeNumber("EMP-" + String.format("%04d", savedEmployee.getEmployeeId()));
        employeeRepository.save(savedEmployee);

        return new EmployeeDtoResponse(savedEmployee);
    }


}
