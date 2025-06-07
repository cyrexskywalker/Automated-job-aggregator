package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.dto.CityCountDTO;
import com.javaproject.vacancy_aggregator.dto.CategoryCountDTO;
import com.javaproject.vacancy_aggregator.dto.SalaryCountDTO;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final VacancyRepository vacancyRepo;

    public AnalyticsService(VacancyRepository vacancyRepo) {
        this.vacancyRepo = vacancyRepo;
    }

    public List<CityCountDTO> countGroupByCity() {
        return vacancyRepo.countGroupByCity().stream()
                .map(arr -> new CityCountDTO(
                        (String) arr[0],
                        (Long)   arr[1])
                )
                .collect(Collectors.toList());
    }

    public List<CategoryCountDTO> countGroupByCategory() {
        return vacancyRepo.countGroupByCategory().stream()
                .map(arr -> new CategoryCountDTO(
                        (String) arr[0],
                        (Long)   arr[1])
                )
                .collect(Collectors.toList());
    }

    public List<SalaryCountDTO> countGroupBySalary() {
        return vacancyRepo.countGroupBySalary().stream()
                .map(arr -> new SalaryCountDTO(
                        (String) arr[0],
                        (Long)   arr[1])
                )
                .collect(Collectors.toList());
    }
}