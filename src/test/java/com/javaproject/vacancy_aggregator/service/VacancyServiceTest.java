package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepo;

    @InjectMocks
    private VacancyService vacancyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_withoutAnyFilter_shouldReturnEmptyPage_andInvokeRepo() {
        // подготовим pageable
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "publicationDate"));
        Page<Vacancy> emptyPage = new PageImpl<>(List.of());
        // мокируем репозиторий на любой Specification и наш pageable
        when(vacancyRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // вызываем сервис без фильтров
        Page<Vacancy> result = vacancyService.findAll(
                null, null, null, null, null,
                pageable
        );

        // проверяем результат
        assertThat(result.getContent()).isEmpty();
        // убедились, что репозиторий вызвали ровно один раз с любым Specification
        verify(vacancyRepo, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAll_withAllFilters_shouldBuildCompositeSpec_andReturnContent() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("salary"));
        Vacancy dummy = new Vacancy();
        Page<Vacancy> page = new PageImpl<>(List.of(dummy));
        // мок на любой Specification
        when(vacancyRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // вызываем сервис со всеми фильтрами
        Page<Vacancy> result = vacancyService.findAll(
                "Москва",
                "ACME",
                "100000",
                "Full-time",
                "Java",
                pageable
        );

        // проверяем, что вернулся наш dummy
        assertThat(result.getContent()).containsExactly(dummy);
        // и репозиторий вызван с ненулевой спецификацией
        verify(vacancyRepo, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}