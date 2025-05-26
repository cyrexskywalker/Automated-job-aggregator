package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.Vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>,
                                           JpaSpecificationExecutor<Vacancy> {
    boolean existsByUrl(String url);
    List<Vacancy> findByCity(String city);
    List<Vacancy> findBySalaryContaining(String salaryPart);
}
