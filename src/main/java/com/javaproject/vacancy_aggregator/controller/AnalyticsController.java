package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.dto.CityCountDTO;
import com.javaproject.vacancy_aggregator.dto.CategoryCountDTO;
import com.javaproject.vacancy_aggregator.dto.SalaryCountDTO;
import com.javaproject.vacancy_aggregator.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/count-by-city")
    public List<CityCountDTO> getCountByCity() {
        return analytics.countGroupByCity();
    }

    @GetMapping("/count-by-category")
    public List<CategoryCountDTO> getCountByCategory() {
        return analytics.countGroupByCategory();
    }

    @GetMapping("/count-by-salary")
    public List<SalaryCountDTO> getCountBySalary() {
        return analytics.countGroupBySalary();
    }
}
