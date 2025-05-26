package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "source")
@Data
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String baseUrl;

    public Source() {

    }

    public Source(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }
}
