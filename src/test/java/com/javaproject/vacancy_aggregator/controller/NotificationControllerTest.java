package com.javaproject.vacancy_aggregator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproject.vacancy_aggregator.domain.NotificationCriteria;
import com.javaproject.vacancy_aggregator.dto.NotificationCriteriaDTO;
import com.javaproject.vacancy_aggregator.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void createCriteria_returnsCreatedObject() throws Exception {
        // prepare DTO
        NotificationCriteriaDTO dto = new NotificationCriteriaDTO();
        dto.setUserEmail("user@example.com");
        dto.setCity("TestCity");
        dto.setCompany("TestCompany");
        dto.setSalary("50000");
        dto.setKeyword("Java");

        // prepare returned entity
        NotificationCriteria saved = new NotificationCriteria();
        saved.setId(1L);
        saved.setUserEmail(dto.getUserEmail());
        saved.setCity(dto.getCity());
        saved.setCompany(dto.getCompany());
        saved.setSalary(dto.getSalary());
        saved.setKeyword(dto.getKeyword());

        given(notificationService.createCriteria(any(NotificationCriteriaDTO.class)))
                .willReturn(saved);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userEmail").value("user@example.com"))
                .andExpect(jsonPath("$.city").value("TestCity"))
                .andExpect(jsonPath("$.company").value("TestCompany"))
                .andExpect(jsonPath("$.salary").value("50000"))
                .andExpect(jsonPath("$.keyword").value("Java"));

        then(notificationService).should()
                .createCriteria(argThat(actual ->
                        actual.getUserEmail().equals("user@example.com") &&
                                actual.getCity().equals("TestCity")
                ));
    }

    @Test
    void getAllCriteria_returnsList() throws Exception {
        NotificationCriteria c1 = new NotificationCriteria();
        c1.setId(1L);
        c1.setUserEmail("a@ex.com");
        c1.setCity("CityA");
        NotificationCriteria c2 = new NotificationCriteria();
        c2.setId(2L);
        c2.setUserEmail("b@ex.com");
        c2.setCity("CityB");

        given(notificationService.getAllCriteria())
                .willReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/notifications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].city").value("CityA"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].city").value("CityB"));

        then(notificationService).should().getAllCriteria();
    }

    @Test
    void updateCriteria_returnsUpdatedObject() throws Exception {
        Long id = 1L;
        NotificationCriteriaDTO dto = new NotificationCriteriaDTO();
        dto.setUserEmail("user2@example.com");
        dto.setCity("NewCity");
        dto.setCompany("NewCompany");
        dto.setSalary("60000");
        dto.setKeyword("Spring");

        NotificationCriteria updated = new NotificationCriteria();
        updated.setId(id);
        updated.setUserEmail(dto.getUserEmail());
        updated.setCity(dto.getCity());
        updated.setCompany(dto.getCompany());
        updated.setSalary(dto.getSalary());
        updated.setKeyword(dto.getKeyword());

        given(notificationService.updateCriteria(eq(id), any(NotificationCriteriaDTO.class)))
                .willReturn(updated);

        mockMvc.perform(put("/api/notifications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.userEmail").value("user2@example.com"))
                .andExpect(jsonPath("$.city").value("NewCity"))
                .andExpect(jsonPath("$.company").value("NewCompany"))
                .andExpect(jsonPath("$.salary").value("60000"))
                .andExpect(jsonPath("$.keyword").value("Spring"));

        then(notificationService).should()
                .updateCriteria(eq(id), argThat(arg ->
                        arg.getCity().equals("NewCity") &&
                                arg.getKeyword().equals("Spring")
                ));
    }

    @Test
    void deleteCriteria_invokesServiceAndReturnsOk() throws Exception {
        Long id = 42L;
        willDoNothing().given(notificationService).deleteCriteria(id);

        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isOk());

        then(notificationService).should().deleteCriteria(id);
    }
}