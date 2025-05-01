package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.Vacancy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    boolean existsByUrl(String url);
    List<Vacancy> findByCity(String city);
    List<Vacancy> findBySalaryContaining(String salaryPart);
}
