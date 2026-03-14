package com.example.employee.management.system.repository;

import com.example.employee.management.system.dto.response.EmployeeDtoResponse;
import com.example.employee.management.system.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment_DepartmentId(Long departmentId);

    @Query("SELECT AVG(employee.salary) FROM Employee employee")
    BigDecimal findAverageSalary();

    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(employee.dateOfBirth)) FROM Employee employee")
    Double findAverageAge();

    @Query("SELECT e FROM Employee e WHERE TIMESTAMPDIFF(YEAR, e.dateOfBirth, CURRENT_DATE) BETWEEN :minAge AND :maxAge")
    List<Employee> findByAgeBetween(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    @Query("SELECT e FROM Employee e WHERE e.department.departmentId = :departmentId AND TIMESTAMPDIFF(YEAR, e.dateOfBirth, CURRENT_DATE) BETWEEN :minAge AND :maxAge")
    List<Employee> findByDepartmentAndAgeBetween(@Param("departmentId") Long departmentId,
                                                 @Param("minAge") int minAge,
                                                 @Param("maxAge") int maxAge);
}
