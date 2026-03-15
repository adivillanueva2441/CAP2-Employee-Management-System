package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.dto.request.DepartmentDtoRequest;
import com.example.employee.management.system.dto.response.DepartmentDtoResponse;
import com.example.employee.management.system.exceptions.DuplicateEntryException;
import com.example.employee.management.system.exceptions.ResourceNotFoundException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional
    @Override
    public List<DepartmentDtoResponse> getAllDepartments() {
        return departmentRepository
                .findAll().stream().map(DepartmentDtoResponse::new).toList();
    }

    @Transactional
    @Override
    public DepartmentDtoResponse getDepartmentById(Long departmentId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id: " + departmentId + " not found"));
        return new DepartmentDtoResponse(department);
    }

    @Transactional
    @Override
    public DepartmentDtoResponse addNewDepartment(DepartmentDtoRequest departmentDtoRequest) {

        if (departmentRepository.existsByDepartmentName(departmentDtoRequest.getDepartmentName())) {
            throw new DuplicateEntryException("Department already exists");
        }

        return createNewDepartment(departmentDtoRequest);
    }

    @Transactional
    @Override
    public void updateDepartmentDetails(Long departmentId, DepartmentDtoRequest departmentDtoRequest){

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(()->new ResourceNotFoundException("Department with id: " + departmentId + " not found"));
        if (departmentRepository.existsByDepartmentName(departmentDtoRequest.getDepartmentName())) {
            throw new DuplicateEntryException("Department already exists");
        }

        department.setDepartmentName(departmentDtoRequest.getDepartmentName());
        departmentRepository.save(department);

    }

    @Transactional
    @Override
    public void deleteDepartment(Long departmentId){
        if(!departmentRepository.existsById(departmentId)){
            throw new ResourceNotFoundException("Department with id: " + departmentId + " not found");
        }
        departmentRepository.deleteById(departmentId);
    }

    private DepartmentDtoResponse createNewDepartment(DepartmentDtoRequest departmentDtoRequest) {
        Department department = new Department();
        department.setDepartmentName(departmentDtoRequest.getDepartmentName());

        Department savedDepartment =  departmentRepository.save(department);
        departmentRepository.save(savedDepartment);

        return new DepartmentDtoResponse(savedDepartment);
    }

}
