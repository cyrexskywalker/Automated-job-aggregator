package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.repository.CompanyRepository;
import com.javaproject.vacancy_aggregator.repository.SourceRepository;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import com.javaproject.vacancy_aggregator.specification.VacancySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VacancyService {
    private final VacancyRepository vacancyRepo;
    private final CompanyRepository companyRepo;
    private final SourceRepository sourceRepo;

    public VacancyService(VacancyRepository vacancyRepository,
                          CompanyRepository companyRepository,
                          SourceRepository sourceRepository) {
        this.vacancyRepo = vacancyRepository;
        this.companyRepo = companyRepository;
        this.sourceRepo = sourceRepository;
    }

    @Transactional
    public void saveAll(List<RawVacancy> vacancies) {
        for (RawVacancy rv : vacancies) {
            if (vacancyRepo.existsByUrl(rv.getUrl())) {
                continue;
            }

            Company company = companyRepo.findByName(rv.getCompany())
                    .orElseGet(() -> companyRepo.save(new Company(rv.getCompany())));
            Source source = sourceRepo.findByName(rv.getSourceName())
                    .orElseGet(() -> sourceRepo.save(new Source(rv.getSourceName(), rv.getSourceUrl())));

            Vacancy vacancy = new Vacancy(source, company, rv.getTitle(), rv.getUrl());
            vacancy.setCity(rv.getCity());
            vacancy.setSalary(rv.getSalary());
            vacancy.setDescription(rv.getDescription());
            vacancy.setPublicationDate(rv.getPublicationDate());

            vacancyRepo.save(vacancy);
        }
    }

    @Transactional(readOnly = true)
    public Page<Vacancy> findAll(
            String city,
            String company,
            String salary,
            Pageable pageable
    ) {
        Specification<Vacancy> spec = Specification.where(null);

        if (city != null && !city.isBlank()) {
            spec = spec.and(VacancySpecifications.cityEquals(city));
        }
        if (company != null && !company.isBlank()) {
            spec = spec.and(VacancySpecifications.companyEquals(company));
        }
        if (salary != null && !salary.isBlank()) {
            spec = spec.and(VacancySpecifications.salaryContains(salary));
        }

        return vacancyRepo.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Vacancy findById(Long id) {
        return vacancyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id не найдена: " + id));
    }
}