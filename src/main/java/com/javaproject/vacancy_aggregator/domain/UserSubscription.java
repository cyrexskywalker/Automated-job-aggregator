package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscription")
@Getter
@Setter
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false) //todo почему то H2 не понимает сущность JSONB
    private String criteria;

    private final LocalDateTime created_at = LocalDateTime.now();

    public UserSubscription() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    public UserSubscription(String criteria) {
        this.criteria = criteria;
    }
}
