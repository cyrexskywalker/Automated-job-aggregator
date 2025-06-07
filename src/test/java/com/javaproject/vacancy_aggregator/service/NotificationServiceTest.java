package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.NotificationCriteria;
import com.javaproject.vacancy_aggregator.dto.NotificationCriteriaDTO;
import com.javaproject.vacancy_aggregator.repository.NotificationCriteriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock NotificationCriteriaRepository criteriaRepo;
    @InjectMocks NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCriteria_shouldReturnDto() {
        // 1) Подготовка входного DTO
        NotificationCriteriaDTO inputDto = new NotificationCriteriaDTO();
        inputDto.setCity("Москва");
        inputDto.setCompany("ACME");
        inputDto.setKeyword("Java");
        inputDto.setSalary("50000");
        inputDto.setUserEmail("test@example.com");
        // … заполните остальные поля при необходимости …

        // 2) Мокаем репозиторий, чтобы при сохранении возвращался сущностный объект
        NotificationCriteria savedEntity = new NotificationCriteria();
        savedEntity.setId(1L);
        savedEntity.setCity(inputDto.getCity());
        savedEntity.setCompany(inputDto.getCompany());
        savedEntity.setKeyword(inputDto.getKeyword());
        savedEntity.setSalary(inputDto.getSalary());
        savedEntity.setUserEmail(inputDto.getUserEmail());
        // … заполните остальные поля при необходимости …

        when(criteriaRepo.save(any(NotificationCriteria.class)))
                .thenReturn(savedEntity);

        // 3) Вызываем метод сервиса
        NotificationCriteria resultDto = notificationService.createCriteria(inputDto);

        // 4) Проверяем, что вернулся DTO с ожидаемыми значениями
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(1L);
        assertThat(resultDto.getCity()).isEqualTo("Москва");
        assertThat(resultDto.getCompany()).isEqualTo("ACME");
        assertThat(resultDto.getKeyword()).isEqualTo("Java");
        assertThat(resultDto.getSalary()).isEqualTo("50000");
        assertThat(resultDto.getUserEmail()).isEqualTo("test@example.com");

        // 5) Убедимся, что репозиторий был вызван ровно один раз
        verify(criteriaRepo, times(1))
                .save(any(NotificationCriteria.class));
    }

    @Test
    void checkAndNotify_shouldCallFindAllCriteria() {
        when(criteriaRepo.findAll()).thenReturn(List.of());
        // Метод не должен выкидывать исключений, если список пуст
        notificationService.checkAndNotify();
        verify(criteriaRepo, times(1)).findAll();
    }
}