package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.dto.VacancyDTO;
import com.javaproject.vacancy_aggregator.service.VacancyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/vacancies")
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    public List<VacancyDTO> getVacancies(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String salary
    ) {
        return vacancyService.findAll(city, company, salary)
                .stream()
                .map(VacancyDTO::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VacancyDTO getVacancyById(@PathVariable("id") Long id) {
        Vacancy vacancy = vacancyService.findById(id);
        return VacancyDTO.from(vacancy);
    }
}
