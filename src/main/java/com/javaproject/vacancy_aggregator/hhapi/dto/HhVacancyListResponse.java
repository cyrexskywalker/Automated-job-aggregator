package com.javaproject.vacancy_aggregator.hhapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class HhVacancyListResponse {
    @JsonProperty("items")
    private List<HhVacancyItem> items;
}