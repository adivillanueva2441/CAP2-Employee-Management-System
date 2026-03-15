package com.example.employee.management.system.controller.api;

import com.example.employee.management.system.dto.request.EmployeeDtoRequest;
import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.service.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeApiController {

    @Autowired
    private IEmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDtoResponse>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDtoResponse> getEmployeeById(@PathVariable Long employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EmployeeDtoResponse>> filterByDepartmentAndAge (@RequestParam(required = false) Long departmentId,
                                                                               @RequestParam(required = false) Integer minAge,
                                                                               @RequestParam(required = false) Integer maxAge) throws Exception{
        if ((minAge != null && maxAge == null) || (minAge == null && maxAge != null)) {
            throw new BadRequestException("Both minAge and maxAge must be provided together");
        }
        if (departmentId != null && minAge != null && maxAge != null) {
            return ResponseEntity.ok(employeeService.filterByDepartmentAndAge(departmentId, minAge, maxAge));
        }
        if (departmentId != null) {
            return ResponseEntity.ok(employeeService.filterByDepartmentId(departmentId));
        }
        if (minAge != null && maxAge != null) {
            return ResponseEntity.ok(employeeService.filterByAgeRange(minAge, maxAge));
        }
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PostMapping
    public ResponseEntity<EmployeeDtoResponse> addNewEmployee(@Valid @RequestBody EmployeeDtoRequest employeeDtoRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addNewEmployee(employeeDtoRequest));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Void> updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody EmployeeDtoRequest employeeDtoRequest){
        employeeService.updateEmployeeDetails(employeeId, employeeDtoRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long employeeId){
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }




}
