package com.example.employee.management.system.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DepartmentDtoRequest {
    @NotBlank(message = "Department name cannot be blank")
    private String departmentName;

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
