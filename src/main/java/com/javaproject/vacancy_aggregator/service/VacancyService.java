package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.repository.CompanyRepository;
import com.javaproject.vacancy_aggregator.repository.SourceRepository;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacancyService {
    private final VacancyRepository vacancyRepo;
    private final CompanyRepository companyRepo;
    private final SourceRepository sourceRepo;

    public VacancyService(VacancyRepository vacancyRepository, CompanyRepository companyRepository, SourceRepository sourceRepository) {
        this.vacancyRepo = vacancyRepository;
        this.companyRepo = companyRepository;
        this.sourceRepo = sourceRepository;
    }

    @Transactional
    public void saveAll(List<RawVacancy> vacancies) {
        for (RawVacancy rv : vacancies) {

            // пропуск дубликатов по URL
            if (vacancyRepo.existsByUrl(rv.getUrl())) {
                continue;
            }

            Company company = companyRepo.findByName(rv.getCompany())
                    .orElseGet(() -> {
                        Company c = new Company(rv.getCompany());
                        return companyRepo.save(c);
                    });
            Source source = sourceRepo.findByName(rv.getSourceName())
                    .orElseGet(() -> {
                        Source s = new Source(rv.getSourceName(), rv.getSourceUrl());
                        return sourceRepo.save(s);
                    });

            Vacancy vacancy = new Vacancy(
                    source,
                    company,
                    rv.getTitle(),
                    rv.getUrl()
            );
            vacancy.setCity(rv.getCity());
            vacancy.setSalary(rv.getSalary());
            vacancy.setDescription(rv.getDescription());
            vacancy.setPublicationDate(rv.getPublicationDate());

            vacancyRepo.save(vacancy);
        }
    }

    @Transactional(readOnly = true)
    public List<Vacancy> findAll(String city, String company, String salary) {
        return vacancyRepo.findAll().stream()
                .filter(v -> city == null || (v.getCity() != null && v.getCity().equalsIgnoreCase(city)))
                .filter(v -> company == null || (v.getCompany() != null && v.getCompany().getName().equalsIgnoreCase(company)))
                .filter(v -> salary == null || (v.getSalary() != null && v.getSalary().toLowerCase().contains(salary.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Vacancy findById(Long id) {
        return vacancyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id не найдена: " + id));
    }
}
