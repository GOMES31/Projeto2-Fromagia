package com.example.fromagiabackend.Service.Supplier;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Entity.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    void save(Supplier supplier);

    Optional<Supplier> findById(Integer id);
    List<Supplier> findCompanySuppliers(Company company);

    List<Supplier> findSuppliersWithoutCompany();

    List<Order> getSupplierOrders(Integer id);

    List<Order> getSupplierCompletedRejectedOrders(Integer id);

    List<Order> getSupplierPendingOrAcceptedOrders(Integer id);
}
