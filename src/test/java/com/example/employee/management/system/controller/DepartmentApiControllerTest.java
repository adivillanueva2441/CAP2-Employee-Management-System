package com.example.employee.management.system.controller;

import com.example.employee.management.system.controller.api.DepartmentApiController;
import com.example.employee.management.system.dto.request.DepartmentDtoRequest;
import com.example.employee.management.system.dto.response.DepartmentDtoResponse;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.service.IDepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentApiControllerTest {

    @Mock
    private IDepartmentService departmentService;

    @InjectMocks
    private DepartmentApiController departmentApiController;

    private Department department;
    private DepartmentDtoResponse departmentDtoResponse;
    private DepartmentDtoRequest departmentDtoRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Engineering");
        department.setEmployees(new ArrayList<>());

        departmentDtoResponse = new DepartmentDtoResponse(department);

        departmentDtoRequest = new DepartmentDtoRequest();
        departmentDtoRequest.setDepartmentName("Engineering");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllDepartments_returns200WithPageOfDepartments() {
        Page<DepartmentDtoResponse> page = new PageImpl<>(List.of(departmentDtoResponse), pageable, 1);
        when(departmentService.getAllDepartments(pageable)).thenReturn(page);

        ResponseEntity<Page<DepartmentDtoResponse>> response = departmentApiController.getAllDepartments(pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getDepartmentName()).isEqualTo(department.getDepartmentName());
        verify(departmentService, times(1)).getAllDepartments(pageable);
    }

    @Test
    void getAllDepartments_returns200WithEmptyPage_whenNoDepartmentsExist() {
        Page<DepartmentDtoResponse> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(departmentService.getAllDepartments(pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<DepartmentDtoResponse>> response = departmentApiController.getAllDepartments(pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).isEmpty();
        verify(departmentService, times(1)).getAllDepartments(pageable);
    }

    @Test
    void getDepartmentById_returns200WithDepartment() {
        when(departmentService.getDepartmentById(1L)).thenReturn(departmentDtoResponse);

        ResponseEntity<DepartmentDtoResponse> response = departmentApiController.getDepartmentById(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getDepartmentId()).isEqualTo(department.getDepartmentId());
        assertThat(response.getBody().getDepartmentName()).isEqualTo(department.getDepartmentName());
        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    @Test
    void addNewDepartment_returns201WithCreatedDepartment() {
        when(departmentService.addNewDepartment(departmentDtoRequest)).thenReturn(departmentDtoResponse);

        ResponseEntity<DepartmentDtoResponse> response = departmentApiController.addNewDepartment(departmentDtoRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().getDepartmentName()).isEqualTo(department.getDepartmentName());
        verify(departmentService, times(1)).addNewDepartment(departmentDtoRequest);
    }

    @Test
    void updateDepartment_returns204() {
        doNothing().when(departmentService).updateDepartmentDetails(1L, departmentDtoRequest);

        ResponseEntity<Void> response = departmentApiController.updateDepartment(1L, departmentDtoRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(departmentService, times(1)).updateDepartmentDetails(1L, departmentDtoRequest);
    }

    @Test
    void deleteDepartment_returns204() {
        doNothing().when(departmentService).deleteDepartment(1L);

        ResponseEntity<Void> response = departmentApiController.deleteDepartment(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}
