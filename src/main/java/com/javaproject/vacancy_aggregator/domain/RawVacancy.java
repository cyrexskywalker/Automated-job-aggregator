package com.javaproject.vacancy_aggregator.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RawVacancy {
    private final String title;
    private final String company;
    private final String city;
    private final String salary;
    private final String description;
    private final LocalDateTime publicationDate;
    private final String url;
    private final String sourceName;
    private final String sourceUrl;

    public RawVacancy(String title,
                      String company,
                      String city,
                      String salary,
                      String description,
                      LocalDateTime publicationDate,
                      String url,
                      String sourceName,
                      String sourceUrl) {
        this.title           = title;
        this.company         = company;
        this.city            = city;
        this.salary          = salary;
        this.description     = description;
        this.publicationDate = publicationDate;
        this.url             = url;
        this.sourceName      = sourceName;
        this.sourceUrl       = sourceUrl;
    }
}
