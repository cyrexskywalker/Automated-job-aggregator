package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.service.VacancyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController controller;

    private Vacancy sampleVacancy() {
        Company company = new Company("TestCo");
        company.setId(1L);
        Source source = new Source("TestSrc", "http://src");
        source.setId(2L);

        Vacancy v = new Vacancy(source, company, "Java Developer", "http://vac1");
        v.setId(42L);
        v.setCity("Moscow");
        v.setSalary("100_000");
        v.setPublicationDate(LocalDateTime.of(2025, 5, 26, 12, 0));
        return v;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice() // если есть общий @ControllerAdvice — можно добавить
                .build();
    }

    @Test
    @DisplayName("GET /api/vacancies без параметров возвращает первую страницу по умолчанию")
    void getVacancies_DefaultPage_ReturnsPage() throws Exception {
        Vacancy v = sampleVacancy();
        PageRequest defaultPage = PageRequest.of(0, 20, Sort.by("publicationDate").descending());
        Page<Vacancy> page = new PageImpl<>(List.of(v), defaultPage, 1);

        when(vacancyService.findAll(isNull(), isNull(), isNull(), eq(defaultPage)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vacancies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(42))
                .andExpect(jsonPath("$.pageable.pageSize").value(20))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(vacancyService).findAll(null, null, null, defaultPage);
    }

    @Test
    @DisplayName("GET /api/vacancies с фильтрами и кастомной пагинацией")
    void getVacancies_WithParamsAndPageable() throws Exception {
        Vacancy v = sampleVacancy();
        PageRequest customPage = PageRequest.of(1, 5, Sort.by("publicationDate").ascending());
        Page<Vacancy> page = new PageImpl<>(List.of(v), customPage, 1);

        when(vacancyService.findAll(eq("Moscow"), eq("TestCo"), eq("100000"), eq(customPage)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vacancies")
                        .param("city", "Moscow")
                        .param("company", "TestCo")
                        .param("salary", "100000")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "publicationDate,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("Moscow"))
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.sort.sorted").value(true));

        verify(vacancyService).findAll("Moscow", "TestCo", "100000", customPage);
    }

    @Test
    @DisplayName("GET /api/vacancies/{id} возвращает DTO, когда есть запись")
    void getVacancyById_Found() throws Exception {
        Vacancy v = sampleVacancy();
        when(vacancyService.findById(42L)).thenReturn(v);

        mockMvc.perform(get("/api/vacancies/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.company").value("TestCo"));

        verify(vacancyService).findById(42L);
    }

    @Test
    @DisplayName("GET /api/vacancies/{id} — 404, если не найдено")
    void getVacancyById_NotFound() throws Exception {
        when(vacancyService.findById(99L))
                .thenThrow(new EntityNotFoundException("Вакансия с таким id не найдена: 99"));

        mockMvc.perform(get("/api/vacancies/99"))
                .andExpect(status().isNotFound());
    }
}