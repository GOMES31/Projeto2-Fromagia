package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Enums.OrderState;
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
    @Transactional(readOnly = true)
    public List<Order> getCompanyAcceptedOrRejectedOrders(Integer id) {

        Company company = companyRepository.findById(id).orElse(null);

        if (Objects.nonNull(company)) {
            Hibernate.initialize(company.getOrders());
            return company.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.ACCEPTED || order.getOrderState() == OrderState.REJECTED)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getCompanyPendingOrders(Integer id) {

        Company company = companyRepository.findById(id).orElse(null);

        if (Objects.nonNull(company)) {
            Hibernate.initialize(company.getOrders());
            return company.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.PENDING)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
