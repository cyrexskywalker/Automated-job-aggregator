package com.javaproject.vacancy_aggregator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VacancyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getVacancies_NoFilters_ReturnsEmptyPage() throws Exception {
        Pageable defaultPageable = PageRequest.of(0, 50, Sort.by("publicationDate").descending());
        Page<Vacancy> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);

        when(vacancyService.findAll(
                any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/vacancies")
                        .param("page", "0")
                        .param("size", "50")
                        .param("sort", "publicationDate,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(vacancyService).findAll(
                isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(defaultPageable));
    }

    @Test
    void getVacancies_WithFilters_ReturnsSingleVacancy() throws Exception {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(42L);
        vacancy.setTitle("Java Developer");
        vacancy.setCompany(new Company("ACME Corp"));
        vacancy.setCity("Москва");
        vacancy.setSalary("120000");
        vacancy.setPublicationDate(LocalDateTime.of(2025, 6, 5, 10, 0));
        vacancy.setUrl("https://example.com/vacancy/42");
        vacancy.setDescription("Ищем Java-разработчика");
        // employmentType больше не учитывается

        Pageable filteredPageable = PageRequest.of(0, 10, Sort.by("salary").ascending());
        Page<Vacancy> singlePage = new PageImpl<>(List.of(vacancy), filteredPageable, 1);

        // теперь на четвёртом месте – isNull(), а keyword – пятый
        when(vacancyService.findAll(
                eq("Москва"),
                eq("ACME Corp"),
                eq("100000"),
                isNull(),
                eq("Java"),
                any(Pageable.class)))
                .thenReturn(singlePage);

        mockMvc.perform(get("/api/vacancies")
                        .param("city", "Москва")
                        .param("company", "ACME Corp")
                        .param("salary", "100000")
                        // убрали .param("employmentType", ...)
                        .param("keyword", "Java")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "salary,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(42))
                .andExpect(jsonPath("$.content[0].title").value("Java Developer"))
                .andExpect(jsonPath("$.content[0].company").value("ACME Corp"))
                .andExpect(jsonPath("$.content[0].city").value("Москва"))
                .andExpect(jsonPath("$.content[0].salary").value("120000"))
                .andExpect(jsonPath("$.content[0].url").value("https://example.com/vacancy/42"));

        verify(vacancyService).findAll(
                eq("Москва"),
                eq("ACME Corp"),
                eq("100000"),
                isNull(),
                eq("Java"),
                eq(filteredPageable));
    }
}