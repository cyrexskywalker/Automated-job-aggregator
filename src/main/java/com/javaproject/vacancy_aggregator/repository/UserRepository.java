package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
