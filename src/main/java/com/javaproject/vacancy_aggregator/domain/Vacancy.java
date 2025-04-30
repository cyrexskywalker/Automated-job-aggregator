package com.javaproject.vacancy_aggregator.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "vacancy")
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    @Getter
    @Setter
    private Source source;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @Getter
    @Setter
    private Company company;

    @Column(nullable = false)
    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String salary;

    @Getter
    @Setter
    private String description;

    @Getter
    private final LocalDateTime publication_date = LocalDateTime.now();

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String url;

    @Getter
    private final LocalDateTime created_at = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "vacancy_category",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Getter
    @Setter
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
