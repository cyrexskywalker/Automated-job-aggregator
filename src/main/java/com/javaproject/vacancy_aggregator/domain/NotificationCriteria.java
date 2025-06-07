package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_criteria")
@Data
public class NotificationCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    private String city;
    private String company;
    private String salary;
    private String keyword;

    @Column(nullable = false)
    private LocalDateTime lastCheckedAt = LocalDateTime.now();
}