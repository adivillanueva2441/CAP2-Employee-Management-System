package com.example.employee.management.system.repository;

import com.example.employee.management.system.model.Department;
import com.example.employee.management.system.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsById(Long departmentId);
    boolean existsByDepartmentName(String departmentName);
    Page<Department> findAll(Pageable pageable);
}
