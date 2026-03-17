package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.request.DepartmentDtoRequest;
import com.example.employee.management.system.dto.response.DepartmentDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDepartmentService {
    Page<DepartmentDtoResponse> getAllDepartments(Pageable pageable);
    DepartmentDtoResponse getDepartmentById(Long departmentId);
    DepartmentDtoResponse addNewDepartment(DepartmentDtoRequest departmentDtoRequest);
    void updateDepartmentDetails(Long departmentId, DepartmentDtoRequest departmentDtoRequest);
    void deleteDepartment(Long departmentId);
}
