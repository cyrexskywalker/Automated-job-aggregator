package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.UserSubscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findById(Long id);
}
