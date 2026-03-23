package com.example.employee.management.system.service.impl;

import com.example.employee.management.system.dto.request.DepartmentDtoRequest;
import com.example.employee.management.system.dto.response.DepartmentDtoResponse;
import com.example.employee.management.system.exceptions.BadRequestException;
import com.example.employee.management.system.exceptions.DuplicateEntryException;
import com.example.employee.management.system.exceptions.ResourceNotFoundException;
import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.repository.DepartmentRepository;
import com.example.employee.management.system.service.IDepartmentService;
import com.example.employee.management.system.service.IMessageHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private IMessageHandlerService messageHandlerService;

    @Transactional
    @Override
    public Page<DepartmentDtoResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository
                .findAll(pageable).map(DepartmentDtoResponse::new);
    }

    @Transactional
    @Override
    public DepartmentDtoResponse getDepartmentById(Long departmentId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("department.not.found", departmentId)));
        return new DepartmentDtoResponse(department);
    }

    @Transactional
    @Override
    public DepartmentDtoResponse addNewDepartment(DepartmentDtoRequest departmentDtoRequest) {

        if (departmentRepository.existsByDepartmentName(departmentDtoRequest.getDepartmentName())) {
            throw new DuplicateEntryException(messageHandlerService.get("department.already.exists", departmentDtoRequest.getDepartmentName()));
        }

        return createNewDepartment(departmentDtoRequest);
    }

    @Transactional
    @Override
    public String updateDepartmentDetails(Long departmentId, DepartmentDtoRequest departmentDtoRequest){

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(()->new ResourceNotFoundException(messageHandlerService.get("department.not.found", departmentId)));
        if (departmentRepository.existsByDepartmentName(departmentDtoRequest.getDepartmentName())) {
            throw new DuplicateEntryException(messageHandlerService.get("department.already.exists", departmentDtoRequest.getDepartmentName()));
        }

        department.setDepartmentName(departmentDtoRequest.getDepartmentName());
        departmentRepository.save(department);

        return messageHandlerService.get("department.updated.success", departmentId);

    }

    @Transactional
    @Override
    public String deleteDepartment(Long departmentId){
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("department.not.found", departmentId)));

        // check if department has employees before deleting
        if (!department.getEmployees().isEmpty()) {
            throw new BadRequestException(messageHandlerService.get("department.has.employees", departmentId));
        }
        departmentRepository.deleteById(departmentId);
        return messageHandlerService.get("department.deleted.success", departmentId);
    }

    private DepartmentDtoResponse createNewDepartment(DepartmentDtoRequest departmentDtoRequest) {
        Department department = new Department();
        department.setDepartmentName(departmentDtoRequest.getDepartmentName());

        Department savedDepartment =  departmentRepository.save(department);
        departmentRepository.save(savedDepartment);

        return new DepartmentDtoResponse(savedDepartment);
    }

}
