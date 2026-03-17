package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEmployeeService {
    Page<EmployeeDtoResponse> getAllEmployees(Pageable pageable);
    EmployeeDtoResponse getEmployeeById(Long employeeId);
    Page<EmployeeDtoResponse> filterByAgeRange(int minAge, int maxAge, Pageable pageable);
    Page<EmployeeDtoResponse> filterByDepartmentId(Long departmentId, Pageable pageable);
    Page<EmployeeDtoResponse> filterByDepartmentAndAge (Long departmentId, int minAge, int maxAge,
                                                        Pageable pageable);
    EmployeeDtoResponse addNewEmployee(EmployeeDtoRequest employeeDtoRequest);
    void updateEmployeeDetails(Long employeeId, EmployeeDtoRequest employeeDtoRequest);
    void deleteEmployee(Long employeeId);
    Page<EmployeeDtoResponse> searchEmployeeName(String employeeName, Pageable pageable);

}
