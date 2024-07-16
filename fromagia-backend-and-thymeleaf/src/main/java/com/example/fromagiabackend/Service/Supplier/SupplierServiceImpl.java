package com.example.fromagiabackend.Service.Supplier;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Enums.OrderState;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Entity.Supplier;
import com.example.fromagiabackend.Repository.SupplierRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService{

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierServiceImpl(SupplierRepository _supplierRepository){
        supplierRepository = _supplierRepository;
    }

    @Override
    public void save(Supplier supplier){
        supplierRepository.save(supplier);
    }

    @Override
    public Optional<Supplier> findById(Integer id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> findCompanySuppliers(Company company) {
        return supplierRepository.findByCompany(company);
    }

    @Override
    public List<Supplier> findSuppliersWithoutCompany(){
        return supplierRepository.findSupplierByCompanyIdIsNull();
    }

    @Override
    @Transactional
    public List<Order> getSupplierOrders(Integer id) {

        Supplier supplier = supplierRepository.findById(id).orElse(null);

        if (Objects.nonNull(supplier)) {
            Hibernate.initialize(supplier.getOrders());
            return supplier.getOrders();
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getSupplierCompletedRejectedOrders(Integer id) {

        Supplier supplier = supplierRepository.findById(id).orElse(null);

        if (Objects.nonNull(supplier)) {
            Hibernate.initialize(supplier.getOrders());
            return supplier.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.COMPLETED || order.getOrderState() == OrderState.REJECTED)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getSupplierPendingOrAcceptedOrders(Integer id) {

        Supplier supplier = supplierRepository.findById(id).orElse(null);

        if (Objects.nonNull(supplier)) {
            Hibernate.initialize(supplier.getOrders());
            return supplier.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.PENDING || order.getOrderState() == OrderState.ACCEPTED)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
