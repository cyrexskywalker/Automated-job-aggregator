package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.NotificationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCriteriaRepository extends JpaRepository<NotificationCriteria, Long> {
}