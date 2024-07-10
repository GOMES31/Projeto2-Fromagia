package com.example.fromagiabackend.Service.Employee;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Employee;
import com.example.fromagiabackend.Entity.Supplier;

import java.util.List;

public interface EmployeeService {

    void save(Employee employee);

    List<Employee> findCompanyEmployees(Company company);
}
