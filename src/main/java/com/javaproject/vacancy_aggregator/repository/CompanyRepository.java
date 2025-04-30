package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByName(String name);
}
