package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Company;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends  JpaRepository<Company, Integer> {

}
