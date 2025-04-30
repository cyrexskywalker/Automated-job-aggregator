package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String name;

    public Company() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    public Company(String name) {
        this.name = name;
    }
}
