package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.dto.VacancyDTO;
import com.javaproject.vacancy_aggregator.service.VacancyService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacancies")
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping
    public Page<VacancyDTO> getVacancies(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String salary,
            @ParameterObject @PageableDefault(page = 0, size = 20, sort = "publicationDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return vacancyService.findAll(city, company, salary, pageable)
                .map(VacancyDTO::from);
    }

    @GetMapping("/{id}")
    public VacancyDTO getVacancyById(@PathVariable Long id) {
        return VacancyDTO.from(vacancyService.findById(id));
    }
}