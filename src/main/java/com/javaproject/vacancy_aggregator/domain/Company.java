package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company")
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Company() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    public Company(String name) {
        this.name = name;
    }
}
