package com.example.fromagiabackend.Service.Supplier;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Supplier;
import com.example.fromagiabackend.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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


}
