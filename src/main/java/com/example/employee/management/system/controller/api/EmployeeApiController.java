package com.example.employee.management.system.controller.api;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.service.IEmployeeService;
import com.example.employee.management.system.service.IMessageHandlerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeApiController {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IMessageHandlerService  messageHandlerService;

    @GetMapping
    public ResponseEntity<Page<EmployeeDtoResponse>> getAllEmployees(Pageable pageable) {
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDtoResponse> getEmployeeById(@PathVariable Long employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeDtoResponse>> searchEmployees(
            @RequestParam String employeeName,
            Pageable pageable) {
        return ResponseEntity.ok(employeeService.searchEmployeeName(employeeName, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<EmployeeDtoResponse>> filterByDepartmentAndAge (
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            Pageable pageable){
        if (departmentId != null && minAge != null && maxAge != null) {
            return ResponseEntity.ok(employeeService.filterByDepartmentAndAge(departmentId, minAge, maxAge
                                                                                ,pageable));
        }
        if (departmentId != null) {
            return ResponseEntity.ok(employeeService.filterByDepartmentId(departmentId, pageable));
        }
        if (minAge != null && maxAge != null) {
            return ResponseEntity.ok(employeeService.filterByAgeRange(minAge, maxAge, pageable));
        }
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @PostMapping
    public ResponseEntity<EmployeeDtoResponse> addNewEmployee(@Valid @RequestBody EmployeeDtoRequest employeeDtoRequest){
        String message = messageHandlerService.get("employee.created.success");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("X-Success-Message", message)
                .body(employeeService.addNewEmployee(employeeDtoRequest));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<String> updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody EmployeeDtoRequest employeeDtoRequest){
        return ResponseEntity.ok(employeeService.updateEmployeeDetails(employeeId, employeeDtoRequest));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId){
        return ResponseEntity.ok(employeeService.deleteEmployee(employeeId));
    }




}
