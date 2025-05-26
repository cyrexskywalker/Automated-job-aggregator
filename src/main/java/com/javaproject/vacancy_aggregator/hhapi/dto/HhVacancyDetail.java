package com.javaproject.vacancy_aggregator.hhapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HhVacancyDetail {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("salary")
    private HhSalary salary;

    @JsonProperty("area")
    private HhArea area;

    @JsonProperty("employer")
    private HhEmployer employer;

    @JsonProperty("published_at")
    private String publishedAt;

    @JsonProperty("alternate_url")
    private String alternateUrl;

    @JsonProperty("description")
    private String description;
}