package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.domain.NotificationCriteria;
import com.javaproject.vacancy_aggregator.dto.NotificationCriteriaDTO;
import com.javaproject.vacancy_aggregator.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public NotificationCriteria createCriteria(@RequestBody NotificationCriteriaDTO dto) {
        return notificationService.createCriteria(dto);
    }

    @GetMapping
    public List<NotificationCriteria> getAllCriteria() {
        return notificationService.getAllCriteria();
    }

    @PutMapping("/{id}")
    public NotificationCriteria updateCriteria(
            @PathVariable Long id,
            @RequestBody NotificationCriteriaDTO dto
    ) {
        return notificationService.updateCriteria(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCriteria(@PathVariable Long id) {
        notificationService.deleteCriteria(id);
    }
}