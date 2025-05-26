package com.javaproject.vacancy_aggregator.hhapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HhArea {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;
}