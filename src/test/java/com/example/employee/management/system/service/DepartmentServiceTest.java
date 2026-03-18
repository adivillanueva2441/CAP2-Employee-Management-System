package com.example.employee.management.system.service;

import com.example.employee.management.system.dto.request.DepartmentDtoRequest;
import com.example.employee.management.system.dto.response.DepartmentDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.exceptions.DuplicateEntryException;
import com.example.employee.management.system.exceptions.ResourceNotFoundException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.service.impl.DepartmentServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Engineering");
        department.setEmployees(new ArrayList<>());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllDepartments_returnsPageOfDepartments() {
        Page<Department> departmentPage = new PageImpl<>(List.of(department), pageable, 1);
        when(departmentRepository.findAll(pageable)).thenReturn(departmentPage);

        Page<DepartmentDtoResponse> response = departmentService.getAllDepartments(pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getDepartmentId()).isEqualTo(department.getDepartmentId());
        assertThat(response.getContent().get(0).getDepartmentName()).isEqualTo(department.getDepartmentName());
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllDepartments_returnsEmptyPage_whenNoDepartmentsExist() {
        Page<Department> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(departmentRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<DepartmentDtoResponse> response = departmentService.getAllDepartments(pageable);

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void getDepartmentById_returnsDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentDtoResponse response = departmentService.getDepartmentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getDepartmentId()).isEqualTo(department.getDepartmentId());
        assertThat(response.getDepartmentName()).isEqualTo(department.getDepartmentName());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void getDepartmentById_throwsResourceNotFoundException_whenDepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department with id: 1 not found");

        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void addNewDepartment_returnsDepartmentDtoResponse() {
        DepartmentDtoRequest request = new DepartmentDtoRequest();
        request.setDepartmentName("Engineering");

        when(departmentRepository.existsByDepartmentName("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentDtoResponse response = departmentService.addNewDepartment(request);

        assertThat(response).isNotNull();
        assertThat(response.getDepartmentName()).isEqualTo(department.getDepartmentName());
        verify(departmentRepository, times(1)).existsByDepartmentName("Engineering");
        verify(departmentRepository, times(2)).save(any(Department.class));
    }

    @Test
    void addNewDepartment_throwsDuplicateEntryException_whenDepartmentAlreadyExists() {
        DepartmentDtoRequest request = new DepartmentDtoRequest();
        request.setDepartmentName("Engineering");

        when(departmentRepository.existsByDepartmentName("Engineering")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.addNewDepartment(request))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessage("Department already exists");

        verify(departmentRepository, never()).save(any(Department.class));
    }


    @Test
    void updateDepartmentDetails_updatesDepartmentSuccessfully() {
        DepartmentDtoRequest request = new DepartmentDtoRequest();
        request.setDepartmentName("HR");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByDepartmentName("HR")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        departmentService.updateDepartmentDetails(1L, request);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).existsByDepartmentName("HR");
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void updateDepartmentDetails_throwsResourceNotFoundException_whenDepartmentNotFound() {
        DepartmentDtoRequest request = new DepartmentDtoRequest();
        request.setDepartmentName("HR");

        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.updateDepartmentDetails(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department with id: 1 not found");

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartmentDetails_throwsDuplicateEntryException_whenDepartmentNameAlreadyExists() {
        DepartmentDtoRequest request = new DepartmentDtoRequest();
        request.setDepartmentName("Engineering");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByDepartmentName("Engineering")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.updateDepartmentDetails(1L, request))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessage("Department already exists");

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void deleteDepartment_deletesDepartmentSuccessfully() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        // department.getEmployees() returns empty list from setUp

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDepartment_throwsResourceNotFoundException_whenDepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department with id: 1 not found");

        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void deleteDepartment_throwsBadRequestException_whenDepartmentHasEmployees() {
        Employee employee = new Employee();
        department.setEmployees(List.of(employee)); // department has employees

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cannot delete department with existing employees.");

        verify(departmentRepository, never()).deleteById(any());
    }


}
