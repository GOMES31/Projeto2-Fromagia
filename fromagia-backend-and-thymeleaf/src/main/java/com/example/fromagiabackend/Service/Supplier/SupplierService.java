package com.example.fromagiabackend.Service.Supplier;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Supplier;

import java.util.List;

public interface SupplierService {

    void save(Supplier supplier);

    List<Supplier> findCompanySuppliers(Company company);

    List<Supplier> findSuppliersWithoutCompany();
}
