package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEmployeeService {
    Page<EmployeeDtoResponse> getAllEmployees(Pageable pageable);
    EmployeeDtoResponse getEmployeeById(Long employeeId);
    Page<EmployeeDtoResponse> filterByAgeRange(Integer minAge, Integer maxAge, Pageable pageable);
    Page<EmployeeDtoResponse> filterByDepartmentId(Long departmentId, Pageable pageable);
    Page<EmployeeDtoResponse> filterByDepartmentAndAge (Long departmentId, Integer minAge, Integer maxAge,
                                                        Pageable pageable);
    EmployeeDtoResponse addNewEmployee(EmployeeDtoRequest employeeDtoRequest);
    String updateEmployeeDetails(Long employeeId, EmployeeDtoRequest employeeDtoRequest);
    String deleteEmployee(Long employeeId);
    Page<EmployeeDtoResponse> searchEmployeeName(String employeeName, Pageable pageable);

}
