package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Category() {
    }

    @ManyToMany(mappedBy = "categories")
    private Set<Vacancy> vacancies = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }
}
