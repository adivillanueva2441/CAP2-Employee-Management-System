package com.example.employee.management.system.controller.api;

import com.example.employee.management.system.dto.request.DepartmentDtoRequest;
import com.example.employee.management.system.dto.response.DepartmentDtoResponse;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.service.IDepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentApiController {

    @Autowired
    private IDepartmentService departmentService;

    @GetMapping
    public ResponseEntity<Page<DepartmentDtoResponse>>  getAllDepartments(Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAllDepartments(pageable));
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentDtoResponse> getDepartmentById(
            @PathVariable Long departmentId) {

        return ResponseEntity.ok(departmentService.getDepartmentById(departmentId));
    }

    @PostMapping
    public ResponseEntity<DepartmentDtoResponse> addNewDepartment(
            @Valid @RequestBody DepartmentDtoRequest departmentDtoRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.addNewDepartment(departmentDtoRequest));
    }

    @PutMapping ("/{departmentId}")
    public ResponseEntity<Void> updateDepartment(
            @PathVariable Long departmentId,
            @Valid @RequestBody DepartmentDtoRequest departmentDtoRequest) {

        departmentService.updateDepartmentDetails(departmentId, departmentDtoRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }



}
