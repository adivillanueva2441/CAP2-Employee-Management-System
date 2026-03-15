package com.example.employee.management.system.dto.response;

import com.example.employee.management.system.model.Department;

public class DepartmentDtoResponse {
    private Long departmentId;
    private String departmentName;
    private int employeeCount;

    public DepartmentDtoResponse(Department department) {
        this.departmentId = department.getDepartmentId();
        this.departmentName = department.getDepartmentName();
        this.employeeCount = department.getEmployees() != null ? department.getEmployees().size() : 0;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }
}
