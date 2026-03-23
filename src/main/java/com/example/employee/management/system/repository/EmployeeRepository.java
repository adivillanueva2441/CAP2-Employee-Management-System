package com.example.employee.management.system.repository;

import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //Calculate average salary
    @Query("SELECT AVG(employee.salary) FROM Employee employee")
    BigDecimal findAverageSalary();

    //Calculate average age
    @Query("SELECT AVG(TIMESTAMPDIFF(YEAR, employee.dateOfBirth, CURRENT_DATE)) FROM Employee employee")
    Double findAverageAge();

    //Find by department ID
    Page<Employee> findByDepartment_DepartmentId(Long departmentId, Pageable pageable);

    //Filter by age range
    @Query("SELECT e FROM Employee e WHERE TIMESTAMPDIFF(YEAR, e.dateOfBirth, CURRENT_DATE)" +
            " BETWEEN :minAge AND :maxAge")
    Page<Employee> findByAgeBetween(@Param("minAge") int minAge, @Param("maxAge") int maxAge, Pageable pageable);

    //Filter by department and age range
    @Query("SELECT e FROM Employee e WHERE e.department.departmentId = :departmentId " +
            "AND TIMESTAMPDIFF(YEAR, e.dateOfBirth, CURRENT_DATE) BETWEEN :minAge AND :maxAge")
    Page<Employee> findByDepartmentAndAgeBetween(@Param("departmentId") Long departmentId,
                                                 @Param("minAge") int minAge,
                                                 @Param("maxAge") int maxAge,
                                                 Pageable pageable);

    //Search for employees based on name
    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameAndDateOfBirth(String name, LocalDate dateOfBirth);
}
