package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Repository.CompanyRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository _companyRepository){
        companyRepository = _companyRepository;
    }

    @Override
    public void save(Company company){
        companyRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getCompanyOrders(Integer id) {

        Company company = companyRepository.findById(id).orElse(null);

        if(Objects.nonNull(company)){
            Hibernate.initialize(company.getOrders());
            return new ArrayList<>(company.getOrders());
        }

        return Collections.emptyList();
    }
}
