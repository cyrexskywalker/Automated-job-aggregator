package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.repository.CompanyRepository;
import com.javaproject.vacancy_aggregator.repository.SourceRepository;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepo;
    @Mock
    private CompanyRepository companyRepo;
    @Mock
    private SourceRepository sourceRepo;

    @InjectMocks
    private VacancyService vacancyService;

    private Company company;
    private Source source;
    private RawVacancy rawNew;
    private RawVacancy rawDup;

    @BeforeEach
    void setUp() {
        company = new Company("TestCo");
        company.setId(1L);
        source = new Source("TestSrc", "http://src");
        source.setId(2L);

        rawNew = new RawVacancy(
                "Dev", "TestCo", "Moscow", "100000",
                "Desc", LocalDateTime.now(),
                "http://vac1", "TestSrc", "http://src"
        );
        rawDup = new RawVacancy(
                "QA", "TestCo", "SPB", "50000",
                "Desc2", LocalDateTime.now(),
                "http://dup", "TestSrc", "http://src"
        );
    }

    @Test
    void saveAll_shouldSkipDuplicatesAndSaveNew() {
        // duplicate по URL
        when(vacancyRepo.existsByUrl("http://dup")).thenReturn(true);
        // для нового — нет дублика
        when(vacancyRepo.existsByUrl("http://vac1")).thenReturn(false);
        // companyRepo не найдёт — создадим
        when(companyRepo.findByName("TestCo")).thenReturn(Optional.empty());
        when(companyRepo.save(any(Company.class))).thenReturn(company);
        // sourceRepo не найдёт — создадим
        when(sourceRepo.findByName("TestSrc")).thenReturn(Optional.empty());
        when(sourceRepo.save(any(Source.class))).thenReturn(source);
        // vacancyRepo.save просто возвращает объект
        when(vacancyRepo.save(any(Vacancy.class))).thenAnswer(inv -> inv.getArgument(0));

        vacancyService.saveAll(List.of(rawDup, rawNew));

        // Проверяем, что для дубликата остался только existsByUrl
        verify(vacancyRepo).existsByUrl("http://dup");
        verifyNoMoreInteractions(vacancyRepo, companyRepo, sourceRepo);

        // Для новой вакансии
        verify(vacancyRepo).existsByUrl("http://vac1");
        verify(companyRepo).findByName("TestCo");
        verify(companyRepo).save(argThat(c -> c.getName().equals("TestCo")));
        verify(sourceRepo).findByName("TestSrc");
        verify(sourceRepo).save(argThat(s -> s.getName().equals("TestSrc")));
        verify(vacancyRepo).save(argThat(v ->
                v.getUrl().equals("http://vac1") &&
                        v.getTitle().equals("Dev") &&
                        v.getCompany().getName().equals("TestCo") &&
                        v.getSource().getName().equals("TestSrc")
        ));
    }

    @Test
    void findById_existing_returnsVacancy() {
        Vacancy v = new Vacancy(source, company, "X", "u");
        v.setId(10L);
        when(vacancyRepo.findById(10L)).thenReturn(Optional.of(v));

        Vacancy found = vacancyService.findById(10L);
        assertThat(found).isSameAs(v);
    }

    @Test
    void findById_notFound_throws() {
        when(vacancyRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vacancyService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_withFilters_callsWithNonNullSpec() {
        Pageable pageReq = PageRequest.of(0, 5, Sort.by("id"));
        Page<Vacancy> mockPage = new PageImpl<>(List.of());
        // любой Specification и Pageable возвращают нашу mockPage
        when(vacancyRepo.findAll(any(Specification.class), eq(pageReq)))
                .thenReturn(mockPage);

        // Проверяем по одному параметру
        assertThat(vacancyService.findAll("Moscow", null, null, pageReq)).isEqualTo(mockPage);
        assertThat(vacancyService.findAll(null, "TestCo", null, pageReq)).isEqualTo(mockPage);
        assertThat(vacancyService.findAll(null, null, "100000", pageReq)).isEqualTo(mockPage);

        // и комбинированно
        assertThat(vacancyService.findAll("Moscow", "TestCo", "100000", pageReq)).isEqualTo(mockPage);

        verify(vacancyRepo, times(4)).findAll(any(Specification.class), eq(pageReq));
    }
}