package com.javaproject.vacancy_aggregator.hhapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HhVacancyItem {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("published_at")
    private String publishedAt;  // ISO-строка (конвертируется далее в LocalDateTime)
}