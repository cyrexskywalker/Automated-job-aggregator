package com.javaproject.vacancy_aggregator.parsing;

import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import com.javaproject.vacancy_aggregator.parser.VacancyParser;
import com.javaproject.vacancy_aggregator.services.VacancyService;
import com.javaproject.vacancy_aggregator.updater.VacancyUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VacancyUpdaterTest {
    @Mock
    private VacancyParser parser;
    @Mock
    private VacancyService service;

    @InjectMocks
    private VacancyUpdater updater;

    private RawVacancy rv;

    @BeforeEach
    void setUp() {
        rv = new RawVacancy(
                "Dev",
                "Co",
                "City",
                "Salary",
                "Desc",
                LocalDateTime.now(),
                "url",
                "src",
                "srcUrl"
        );
    }

    @Test
    void fetchAndSave_invokesParserAndService() throws NoSuchFieldException, IllegalAccessException {
        List<String> urls = List.of("u1", "u2");
        // inject test urls via reflection or setter; here assume field is accessible
        // alternatively, instantiate updater manually
        VacancyUpdater testUpdater = new VacancyUpdater(parser, service);
        // set urls via reflection
        var field = VacancyUpdater.class.getDeclaredField("urls");
        field.setAccessible(true);
        field.set(testUpdater, urls);

        when(parser.parse("u1")).thenReturn(List.of(rv));
        when(parser.parse("u2")).thenReturn(List.of());

        testUpdater.fetchAndSave();

        ArgumentCaptor<List<RawVacancy>> cap = ArgumentCaptor.forClass(List.class);
        verify(parser).parse("u1");
        verify(parser).parse("u2");
        verify(service).saveAll(cap.capture());
        assertThat(cap.getValue()).containsExactly(rv);
    }
}
