package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;

import java.util.List;

public interface IEmployeeService {
    List<EmployeeDtoResponse> getAllEmployees();
    EmployeeDtoResponse getEmployeeById(Long employeeId);
    List<EmployeeDtoResponse> filterByAgeRange(int minAge, int maxAge);
    List<EmployeeDtoResponse> filterByDepartmentId(Long departmentId);
    public List<EmployeeDtoResponse> filterByDepartmentAndAge (Long departmentId, int minAge, int maxAge);
    EmployeeDtoResponse addNewEmployee(EmployeeDtoRequest employeeDtoRequest);
    void updateEmployeeDetails(Long employeeId, EmployeeDtoRequest employeeDtoRequest);
    void deleteEmployee(Long employeeId);
}
