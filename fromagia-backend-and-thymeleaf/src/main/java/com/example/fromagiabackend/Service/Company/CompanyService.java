package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    void save(Company company);


    Optional<Company> findById(Integer id);

    List<Order> getCompanyAcceptedOrRejectedOrders(Integer id);

    List<Order> getCompanyPendingOrders(Integer id);
}
