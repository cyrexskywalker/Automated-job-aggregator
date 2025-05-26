package com.javaproject.vacancy_aggregator.updater;

import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.parser.VacancyParser;
import com.javaproject.vacancy_aggregator.service.VacancyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacancyUpdater {

    private final VacancyParser parser;
    private final VacancyService service;

    public VacancyUpdater(VacancyParser vacancyParser, VacancyService vacancyService) {
        this.parser = vacancyParser;
        this.service = vacancyService;
    }

    private final List<String> searchQueries = List.of(
            "Java Developer",
            "Spring Boot",
            "Microservices"
    );

    @Scheduled(fixedDelay = 60 * 60 * 1000) // каждый час
    public void fetchAndSave() {
        for (String query : searchQueries) {
            List<RawVacancy> rawList = parser.parse(query);
            if (!rawList.isEmpty()) {
                service.saveAll(rawList);
            }
        }
    }
}