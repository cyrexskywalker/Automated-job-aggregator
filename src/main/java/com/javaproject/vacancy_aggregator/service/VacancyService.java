package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.*;
import com.javaproject.vacancy_aggregator.dto.GroupCountDTO;
import com.javaproject.vacancy_aggregator.repository.CategoryRepository;
import com.javaproject.vacancy_aggregator.repository.CompanyRepository;
import com.javaproject.vacancy_aggregator.repository.SourceRepository;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import com.javaproject.vacancy_aggregator.specification.VacancySpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class VacancyService {
    private final VacancyRepository vacancyRepo;
    private final CompanyRepository companyRepo;
    private final SourceRepository sourceRepo;
    private final CategoryRepository categoryRepo;

    private final EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(VacancyService.class);

    public VacancyService(VacancyRepository vacancyRepository,
                          CompanyRepository companyRepository,
                          SourceRepository sourceRepository,
                          CategoryRepository categoryRepo,
                          EntityManager em) {
        this.vacancyRepo = vacancyRepository;
        this.companyRepo = companyRepository;
        this.sourceRepo = sourceRepository;
        this.categoryRepo = categoryRepo;
        this.em = em;
    }

    @Transactional
    public void saveAll(List<RawVacancy> vacancies) {
        for (RawVacancy rv : vacancies) {
            if (vacancyRepo.existsByUrl(rv.getUrl())) {
                continue;
            }

            List<Category> allCategories = categoryRepo.findAll();

            Company company = companyRepo.findByName(rv.getCompany())
                    .orElseGet(() -> companyRepo.save(new Company(rv.getCompany())));
            Source source = sourceRepo.findByName(rv.getSourceName())
                    .orElseGet(() -> sourceRepo.save(new Source(rv.getSourceName(), rv.getSourceUrl())));

            Vacancy vacancy = new Vacancy(source, company, rv.getTitle(), rv.getUrl());
            vacancy.setCity(rv.getCity());
            vacancy.setSalary(rv.getSalary());
            vacancy.setEmploymentType(rv.getSourceName());
            vacancy.setDescription(rv.getDescription());
            vacancy.setPublicationDate(rv.getPublicationDate());

            if (rv.getTitle() != null && !rv.getTitle().isBlank()) {
                String titleLower = rv.getTitle().toLowerCase(Locale.ROOT);

                for (Category cat : allCategories) {
                    String catNameLower = cat.getName().toLowerCase(Locale.ROOT);
                    if (titleLower.contains(catNameLower)) {
                        vacancy.getCategories().add(cat);
                    }
                }
            }

            try {
                vacancyRepo.save(vacancy);
            } catch (DataIntegrityViolationException ex) {
                log.warn("Вставка вакансии '{}' пропущена – дубликат URL: {}", vacancy.getTitle(), vacancy.getUrl());
                em.clear();
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<Vacancy> findAll(
            String city,
            String company,
            String salary,
            String employmentType,
            String keyword,
            Pageable pageable
    ) {
        System.out.println(">>> [Service] findAll(...) called with " +
                "city=" + city +
                ", company=" + company +
                ", salary=" + salary +
                ", employmentType=" + employmentType +
                ", keyword=" + keyword +
                ", pageable=" + pageable.getPageNumber() +
                "×" + pageable.getPageSize() +
                ", sort=" + pageable.getSort()
        );
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
        if (employmentType != null && !employmentType.isBlank()) {
            spec = spec.and(VacancySpecifications.employmentEquals(employmentType));
        }
        if (keyword != null && !keyword.isBlank()) {
            System.out.println(">>> [Service] добавляем спецификацию textContainsKeyword(" + keyword + ")");
            spec = spec.and(VacancySpecifications.textContainsKeyword(keyword));
        }
        Page<Vacancy> page = vacancyRepo.findAll(spec, pageable);
        System.out.println(">>> [Service] returned " + page.getTotalElements() + " вакансий");
        return page;
    }

    @Transactional(readOnly = true)
    public Vacancy findById(Long id) {
        return vacancyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id не найдена: " + id));
    }

    /**
     * Возвращает список GroupCountDTO, где key = город, count = число вакансий в этом городе
     */
    @Transactional(readOnly = true)
    public List<GroupCountDTO> groupByCity() {
        return vacancyRepo.countGroupByCity().stream()
                .map(arr -> new GroupCountDTO(
                        (String) arr[0],
                        (Long)   arr[1]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список GroupCountDTO, где key = название категории, count = число вакансий в этой категории
     */
    @Transactional(readOnly = true)
    public List<GroupCountDTO> groupByCategory() {
        return vacancyRepo.countGroupByCategory().stream()
                .map(arr -> new GroupCountDTO(
                        (String) arr[0],
                        (Long)   arr[1]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список GroupCountDTO, где key = строка salary, count = число вакансий с такой salary
     */
    @Transactional(readOnly = true)
    public List<GroupCountDTO> groupBySalary() {
        return vacancyRepo.countGroupBySalary().stream()
                .map(arr -> new GroupCountDTO(
                        (String) arr[0],
                        (Long)   arr[1]
                ))
                .collect(Collectors.toList());
    }
}