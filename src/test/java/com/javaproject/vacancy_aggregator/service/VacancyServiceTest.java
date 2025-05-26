package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import com.javaproject.vacancy_aggregator.repository.SourceRepository;
import com.javaproject.vacancy_aggregator.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepo;

    @Mock
    private SourceRepository sourceRepo;

    @Mock
    private CompanyRepository companyRepo;

    @InjectMocks
    private VacancyService vacancyService;

    private RawVacancy raw;
    private Source src;
    private Company comp;

    @BeforeEach
    void setUp() {
        src = new Source("hh.ru", "https://hh.ru");
        comp = new Company("ExampleCorp");
        raw = new RawVacancy(
                "Java Developer",
                comp.getName(),
                "Moscow",
                "100k-200k",
                "Desc",
                LocalDateTime.of(2025, 1, 1, 12, 0),
                "https://hh.ru/vacancy/1",
                src.getName(),
                src.getBaseUrl()
        );
    }

    @Test
    void saveAll_skipsWhenExists() {
        when(vacancyRepo.existsByUrl(raw.getUrl())).thenReturn(true);

        vacancyService.saveAll(List.of(raw));

        verify(vacancyRepo, never()).save(any(Vacancy.class));
    }

    @Test
    void saveAll_createsSourceCompanyAndVacancy() {
        when(vacancyRepo.existsByUrl(raw.getUrl())).thenReturn(false);
        when(sourceRepo.findByName(raw.getSourceName())).thenReturn(Optional.empty());
        when(companyRepo.findByName(raw.getCompany())).thenReturn(Optional.empty());
        when(sourceRepo.save(any(Source.class))).thenAnswer(i -> i.getArgument(0));
        when(companyRepo.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));
        when(vacancyRepo.save(any(Vacancy.class))).thenAnswer(i -> i.getArgument(0));

        vacancyService.saveAll(List.of(raw));

        ArgumentCaptor<Source> srcCap = ArgumentCaptor.forClass(Source.class);
        verify(sourceRepo).save(srcCap.capture());
        assertThat(srcCap.getValue().getName()).isEqualTo(raw.getSourceName());

        ArgumentCaptor<Company> compCap = ArgumentCaptor.forClass(Company.class);
        verify(companyRepo).save(compCap.capture());
        assertThat(compCap.getValue().getName()).isEqualTo(raw.getCompany());

        ArgumentCaptor<Vacancy> vacCap = ArgumentCaptor.forClass(Vacancy.class);
        verify(vacancyRepo).save(vacCap.capture());
        Vacancy saved = vacCap.getValue();
        assertThat(saved.getTitle()).isEqualTo(raw.getTitle());
        assertThat(saved.getUrl()).isEqualTo(raw.getUrl());
    }

    @Test
    void findAll_filtersByCityCompanyAndSalary() {
        Vacancy v1 = new Vacancy(src, comp, "A", "url1");
        v1.setCity("Moscow");
        v1.setSalary("100k");
        Vacancy v2 = new Vacancy(src, comp, "B", "url2");
        v2.setCity("Saint-Petersburg");
        v2.setSalary("200k");
        when(vacancyRepo.findAll()).thenReturn(List.of(v1, v2));

        List<Vacancy> byCity = vacancyService.findAll("moscow", null, null);
        assertThat(byCity).containsExactly(v1);

        List<Vacancy> bySalary = vacancyService.findAll(null, null, "200");
        assertThat(bySalary).containsExactly(v2);

        List<Vacancy> both = vacancyService.findAll("moscow", null, "100");
        assertThat(both).containsExactly(v1);

        List<Vacancy> all = vacancyService.findAll(null, null, null);
        assertThat(all).containsExactlyInAnyOrder(v1, v2);
    }

    @Test
    void findById_returnsWhenExists() {
        Vacancy v = new Vacancy(src, comp, "C", "url3");
        when(vacancyRepo.findById(3L)).thenReturn(Optional.of(v));

        Vacancy found = vacancyService.findById(3L);
        assertThat(found).isSameAs(v);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(vacancyRepo.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.findById(42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Вакансия с таким id не найдена: 42");
    }
}

