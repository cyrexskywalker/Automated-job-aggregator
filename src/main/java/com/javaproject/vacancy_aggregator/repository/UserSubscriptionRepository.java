package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.UserSubscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findById(Long id);
}
