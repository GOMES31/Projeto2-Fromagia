package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Enums.OrderState;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Repository.CompanyRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    public Optional<Company> findById(Integer id) {
        Optional<Company> company = companyRepository.findById(id);
        company.ifPresent(c -> Hibernate.initialize(c.getProductionHistory()));
        return company;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Order> getCompanyCompletedRejectedOrders(Integer id) {
        Company company = companyRepository.findById(id).orElse(null);

        if (Objects.nonNull(company)) {
            Hibernate.initialize(company.getOrders());
            return company.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.COMPLETED || order.getOrderState() == OrderState.REJECTED )
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getCompanyPendingOrAcceptedOrders(Integer id) {

        Company company = companyRepository.findById(id).orElse(null);

        if (Objects.nonNull(company)) {
            Hibernate.initialize(company.getOrders());
            return company.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.PENDING || order.getOrderState() == OrderState.ACCEPTED)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional
    public List<Order> getCompanyOrders(Integer id) {

        Company company = companyRepository.findById(id).orElse(null);

        if (Objects.nonNull(company)) {
            Hibernate.initialize(company.getOrders());
            return company.getOrders();
        }

        return Collections.emptyList();
    }
    @Override
    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }


}
