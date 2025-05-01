package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "vacancy")
@Getter
@Setter
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private String title;

    private String city;

    private String salary;

    private String description;

    private LocalDateTime publication_date = LocalDateTime.now();

    @Column(nullable = false, unique = true)
    private String url;

    private LocalDateTime created_at = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "vacancy_category",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    public Vacancy() {
        //нужно для JPA (требует конструктор без аргументов)
    }

    public Vacancy(Source sourceId, Company companyId, String title, String url) {
        this.source = sourceId;
        this.company = companyId;
        this.title = title;
        this.url = url;
    }
}
