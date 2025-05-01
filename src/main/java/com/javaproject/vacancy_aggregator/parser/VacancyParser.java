package com.javaproject.vacancy_aggregator.parser;

import com.javaproject.vacancy_aggregator.domain.RawVacancy;

import java.util.List;

public interface VacancyParser {
    List<RawVacancy> parse(String text);
}
