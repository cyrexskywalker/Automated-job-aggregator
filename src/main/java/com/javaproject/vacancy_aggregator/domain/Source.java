package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "source")
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false)
    @Getter
    @Setter
    private String baseUrl;

    public Source() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    public Source(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }
}
