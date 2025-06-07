package com.javaproject.vacancy_aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javaproject.vacancy_aggregator.domain.Source;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Integer> {
    Optional<Source> findByName(String name);
}
