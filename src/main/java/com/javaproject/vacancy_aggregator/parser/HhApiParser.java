package com.javaproject.vacancy_aggregator.parser;

import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.hhapi.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class HhApiParser implements VacancyParser {

    private final WebClient client;
    private static final DateTimeFormatter PUBLISH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String SOURCE_NAME = "HH API";
    private static final String SOURCE_BASE_URL = "https://api.hh.ru";

    public HhApiParser(WebClient hhWebClient) {
        this.client = hhWebClient;
    }

    @Override
    public List<RawVacancy> parse(String searchText) {
        // Получить список вакансий
        Mono<HhVacancyListResponse> listResponse = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/vacancies")
                        .queryParam("text", searchText)
                        .queryParam("per_page", 100)
                        .build())
                .retrieve()
                .bodyToMono(HhVacancyListResponse.class);

        HhVacancyListResponse resp = listResponse.block();
        if (resp == null || resp.getItems() == null || resp.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        // Для каждого элемента получить детали и конвертировать
        return resp.getItems().stream()
                .map(item -> fetchDetail(item.getId()))
                .filter(Objects::nonNull)
                .map(this::toRawVacancy)
                .collect(Collectors.toList());
    }

    private HhVacancyDetail fetchDetail(String id) {
        try {
            return client.get()
                    .uri("/vacancies/{id}", id)
                    .retrieve()
                    .bodyToMono(HhVacancyDetail.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    private RawVacancy toRawVacancy(HhVacancyDetail d) {
        String title = d.getName();

        String company;
        if (d.getEmployer() != null) {
            company = d.getEmployer().getName();
        } else {
            company = null;
        }

        String city;
        if (d.getArea() != null) {
            city = d.getArea().getName();
        } else {
            city = null;
        }

        String salary = formatSalary(d.getSalary());
        String description = d.getDescription();
        LocalDateTime publishedAt = parsePublishedAt(d.getPublishedAt());
        String url = d.getAlternateUrl();

        return new RawVacancy(
                title,
                company,
                city,
                salary,
                description,
                publishedAt,
                url,
                SOURCE_NAME,
                SOURCE_BASE_URL
        );
    }

    private String formatSalary(HhSalary s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (s.getFrom() != null) {
            sb.append(s.getFrom());
        }
        if (s.getTo() != null) {
            if (sb.length() > 0) {
                sb.append("–");
            }
            sb.append(s.getTo());
        }
        if (sb.length() > 0 && s.getCurrency() != null) {
            sb.append(' ').append(s.getCurrency());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private LocalDateTime parsePublishedAt(String publishedAt) {
        if (publishedAt == null) {
            return null;
        }
        OffsetDateTime odt = OffsetDateTime.parse(publishedAt, PUBLISH_DATE_FORMATTER);
        return odt.toLocalDateTime();
    }
}