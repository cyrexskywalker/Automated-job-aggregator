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

    // Список URL для парсинга
    private List<String> urls = List.of(
            "https://hh.ru/search/vacancy?text=Java+Developer"
    );

    @Scheduled(fixedDelay = 60 * 60 * 1000) // каждый час
    public void fetchAndSave() {
        for (String url : urls) {
            // возвращаем список найденных вакансий
            List<RawVacancy> rawList = parser.parse(url);
            // сохраняем только если есть новые вакансии
            if (!rawList.isEmpty()) {
                service.saveAll(rawList);
            }
        }
    }
}
