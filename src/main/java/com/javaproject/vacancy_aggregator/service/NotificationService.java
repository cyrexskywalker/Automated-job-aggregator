package com.javaproject.vacancy_aggregator.service;

import com.javaproject.vacancy_aggregator.domain.NotificationCriteria;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import com.javaproject.vacancy_aggregator.dto.NotificationCriteriaDTO;
import com.javaproject.vacancy_aggregator.repository.NotificationCriteriaRepository;
import com.javaproject.vacancy_aggregator.repository.VacancyRepository;
import com.javaproject.vacancy_aggregator.specification.VacancySpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationCriteriaRepository criteriaRepo;
    private final VacancyRepository vacancyRepo;

    public NotificationService(NotificationCriteriaRepository criteriaRepo,
                               VacancyRepository vacancyRepo) {
        this.criteriaRepo = criteriaRepo;
        this.vacancyRepo = vacancyRepo;
    }

    @Transactional
    public NotificationCriteria createCriteria(NotificationCriteriaDTO dto) {
        NotificationCriteria criteria = new NotificationCriteria();
        criteria.setUserEmail(dto.getUserEmail());
        criteria.setCity(dto.getCity());
        criteria.setCompany(dto.getCompany());
        criteria.setSalary(dto.getSalary());
        criteria.setKeyword(dto.getKeyword());
        return criteriaRepo.save(criteria);
    }

    @Transactional
    public NotificationCriteria updateCriteria(Long id, NotificationCriteriaDTO dto) {
        NotificationCriteria criteria = criteriaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Критерий уведомления не найден: " + id));

        criteria.setCity(dto.getCity());
        criteria.setCompany(dto.getCompany());
        criteria.setSalary(dto.getSalary());
        criteria.setKeyword(dto.getKeyword());

        return criteriaRepo.save(criteria);
    }

    @Transactional
    public void deleteCriteria(Long id) {
        NotificationCriteria criteria = criteriaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Критерий уведомления не найден: " + id));
        criteriaRepo.delete(criteria);
    }


    @Transactional(readOnly = true)
    public List<NotificationCriteria> getAllCriteria() {
        return criteriaRepo.findAll();
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000) // каждые 5 минут
    @Transactional
    public void checkAndNotify() {
        List<NotificationCriteria> allCriteria = criteriaRepo.findAll();

        for (NotificationCriteria criteria : allCriteria) {
            LocalDateTime lastChecked = criteria.getLastCheckedAt();

            Specification<Vacancy> spec = Specification.where(null);

            spec = spec.and((root, cq, cb) ->
                    cb.greaterThan(root.get("publicationDate"), lastChecked)
            );

            if (criteria.getCity() != null && !criteria.getCity().isBlank()) {
                spec = spec.and(VacancySpecifications.cityEquals(criteria.getCity()));
            }
            if (criteria.getCompany() != null && !criteria.getCompany().isBlank()) {
                spec = spec.and(VacancySpecifications.companyEquals(criteria.getCompany()));
            }
            if (criteria.getSalary() != null && !criteria.getSalary().isBlank()) {
                spec = spec.and(VacancySpecifications.salaryContains(criteria.getSalary()));
            }
            if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
                spec = spec.and(VacancySpecifications.textContainsKeyword(criteria.getKeyword()));
            }

            List<Vacancy> newVacancies = vacancyRepo.findAll(spec, Sort.by(Sort.Direction.ASC, "publicationDate"));

            if (!newVacancies.isEmpty()) {
                for (Vacancy v : newVacancies) {
                    // Здесь должна быть реальная логика рассылки: email, push, telegram и т.п.
                    // Пока просто в логах:
                    log.info("Уведомление для {}: найдена новая вакансия '{}', URL={}",
                            criteria.getUserEmail(),
                            v.getTitle(),
                            v.getUrl()
                    );
                }
                criteria.setLastCheckedAt(LocalDateTime.now());
                criteriaRepo.save(criteria);
            }
        }
    }
}