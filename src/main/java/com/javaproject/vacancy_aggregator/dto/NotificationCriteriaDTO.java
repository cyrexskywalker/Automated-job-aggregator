package com.javaproject.vacancy_aggregator.dto;

import lombok.Data;

@Data
public class NotificationCriteriaDTO {
    private String userEmail;
    private String city;
    private String company;
    private String salary;
    private String keyword;
}