package com.example.fromagiabackend.Service.Company;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
