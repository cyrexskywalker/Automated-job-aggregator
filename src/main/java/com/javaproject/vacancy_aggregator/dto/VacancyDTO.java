package com.javaproject.vacancy_aggregator.dto;

import com.javaproject.vacancy_aggregator.domain.Vacancy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDTO {
    private Long id;
    private String title;
    private String company;
    private String city;
    private String salary;
    private String url;
    private LocalDateTime publicationDate;

    public static VacancyDTO from(Vacancy vacancy) {
        VacancyDTO dto = new VacancyDTO();
        dto.setId(vacancy.getId());
        dto.setTitle(vacancy.getTitle());
        dto.setCompany(vacancy.getCompany().getName());
        dto.setCity(vacancy.getCity());
        dto.setSalary(vacancy.getSalary());
        dto.setUrl(vacancy.getUrl());
        dto.setPublicationDate(vacancy.getPublicationDate());
        return dto;
    }
}
