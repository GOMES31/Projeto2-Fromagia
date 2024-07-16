package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    void save(Company company);


    Optional<Company> findById(Integer id);

    List<Order> getCompanyDeliveredRejectedReceivedOrders(Integer id);

    List<Order> getCompanyPendingOrAcceptedOrders(Integer id);

    List<Order> getCompanyOrders(Integer id);

    List<Company> findAllCompanies();
}
