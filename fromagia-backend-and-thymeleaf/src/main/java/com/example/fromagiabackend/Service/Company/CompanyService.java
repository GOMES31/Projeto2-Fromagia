package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;

import java.util.List;

public interface CompanyService {

    void save(Company company);

    List<Order> getCompanyOrders(Integer id);
}
