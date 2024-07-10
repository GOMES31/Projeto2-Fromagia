package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Employee;
import com.example.fromagiabackend.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

    List<Employee> findByCompany(Company company);
}
