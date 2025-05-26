package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.service.VacancyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VacancyController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class VacancyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VacancyService vacancyService;

    private Source src;
    private Company comp;

    @BeforeEach
    void setUp() {
        src = new Source("test-source", "http://test-url");
        comp = new Company("TestCompany");
    }

    @Test
    void GET_vacancies_withoutFilters_returnsAll() throws Exception {
        Vacancy v1 = new Vacancy(src, comp, "DevA", "url1");
        v1.setCity("Moscow");
        v1.setSalary("100k");
        v1.setPublicationDate(LocalDateTime.of(2025, 1, 1, 10, 0));

        Vacancy v2 = new Vacancy(src, comp, "DevB", "url2");
        v2.setCity("SPB");
        v2.setSalary("200k");
        v2.setPublicationDate(LocalDateTime.of(2025, 1, 2, 11, 0));

        when(vacancyService.findAll(isNull(), isNull(), isNull()))
                .thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/vacancies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("DevA"))
                .andExpect(jsonPath("$[1].title").value("DevB"));
    }

    @Test
    void GET_vacancies_withCityFilter_appliesFilter() throws Exception {
        Vacancy v = new Vacancy(src, comp, "DevCity", "url3");
        v.setCity("Moscow");
        v.setSalary("150k");
        v.setPublicationDate(LocalDateTime.now());

        when(vacancyService.findAll(eq("Moscow"), isNull(), isNull()))
                .thenReturn(List.of(v));

        mockMvc.perform(get("/api/vacancies")
                        .param("city", "Moscow")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city").value("Moscow"));
    }

    @Test
    void GET_vacancies_byId_found() throws Exception {
        Vacancy v = new Vacancy(src, comp, "DevDetail", "url4");
        v.setCity("CityX");
        v.setSalary("50k");
        v.setPublicationDate(LocalDateTime.now());

        when(vacancyService.findById(5L)).thenReturn(v);

        mockMvc.perform(get("/api/vacancies/5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("DevDetail"))
                .andExpect(jsonPath("$.url").value("url4"));
    }

    @Test
    void GET_vacancies_byId_notFound() throws Exception {
        when(vacancyService.findById(99L))
                .thenThrow(new EntityNotFoundException("Вакансия с таким id не найдена: 99"));

        mockMvc.perform(get("/api/vacancies/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}