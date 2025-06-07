package com.javaproject.vacancy_aggregator.specification;

import com.javaproject.vacancy_aggregator.domain.Vacancy;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;

class VacancySpecificationsTest {

    @Test
    void cityEquals_shouldReturnSpecification() {
        Specification<Vacancy> spec = VacancySpecifications.cityEquals("Москва");
        assertThat(spec).isNotNull();
    }

    @Test
    void companyEquals_shouldReturnSpecification() {
        Specification<Vacancy> spec = VacancySpecifications.companyEquals("Acme");
        assertThat(spec).isNotNull();
    }

    @Test
    void salaryContains_shouldReturnSpecification() {
        Specification<Vacancy> spec = VacancySpecifications.salaryContains("100000");
        assertThat(spec).isNotNull();
    }

    @Test
    void employmentEquals_shouldReturnSpecification() {
        Specification<Vacancy> spec = VacancySpecifications.employmentEquals("Full-time");
        assertThat(spec).isNotNull();
    }

    @Test
    void textContainsKeyword_shouldReturnSpecification() {
        Specification<Vacancy> spec = VacancySpecifications.textContainsKeyword("Java");
        assertThat(spec).isNotNull();
    }
}