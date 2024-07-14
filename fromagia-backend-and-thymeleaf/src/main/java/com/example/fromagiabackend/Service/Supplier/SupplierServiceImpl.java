package com.example.fromagiabackend.Service.Supplier;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Entity.Supplier;
import com.example.fromagiabackend.Repository.SupplierRepository;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DialectOverride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public List<Supplier> findCompanySuppliers(Company company) {
        return supplierRepository.findByCompany(company);
    }

    @Override
    public List<Supplier> findSuppliersWithoutCompany(){
        return supplierRepository.findByCompanyIsNull();
    }


}
