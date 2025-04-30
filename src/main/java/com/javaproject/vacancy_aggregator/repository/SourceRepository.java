package com.javaproject.vacancy_aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javaproject.vacancy_aggregator.domain.Source;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Integer> {
    Optional<Source> findByName(String name);
}
