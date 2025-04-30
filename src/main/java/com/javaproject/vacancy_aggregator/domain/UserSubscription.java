package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscription")
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(columnDefinition = "JSONB", nullable = false)
    @Getter
    @Setter
    private String criteria;

    @Getter
    private final LocalDateTime created_at = LocalDateTime.now();

    public UserSubscription() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    public UserSubscription(String criteria) {
        this.criteria = criteria;
    }
}
