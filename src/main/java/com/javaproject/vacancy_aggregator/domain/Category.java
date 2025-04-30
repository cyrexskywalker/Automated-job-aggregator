package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    public Category() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    @ManyToMany(mappedBy = "categories")
    @Getter
    @Setter
    private Set<Vacancy> vacancies = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }
}
