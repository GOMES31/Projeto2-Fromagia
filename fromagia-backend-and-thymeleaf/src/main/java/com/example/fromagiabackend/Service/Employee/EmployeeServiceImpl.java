package com.example.fromagiabackend.Service.Employee;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Employee;
import com.example.fromagiabackend.Entity.Supplier;
import com.example.fromagiabackend.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository _employeeRepository){
        employeeRepository = _employeeRepository;
    }
    @Override
    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findCompanyEmployees(Company company) {
        return employeeRepository.findByCompany(company);
    }
}
