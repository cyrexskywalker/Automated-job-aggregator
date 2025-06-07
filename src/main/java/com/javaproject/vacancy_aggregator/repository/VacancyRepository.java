package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.Vacancy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long>,
                                           JpaSpecificationExecutor<Vacancy> {
    boolean existsByUrl(String url);

    /**
     * Возвращает список Object[], где
     * [0] = v.city (String),
     * [1] = COUNT(v) (Long)
     */
    @Query("""
        SELECT v.city   AS city,
               COUNT(v) AS cnt
        FROM   Vacancy v
        WHERE  v.city IS NOT NULL
        GROUP  BY v.city
        """)
    List<Object[]> countGroupByCity();

    /**
     * Возвращает список Object[], где
     * [0] = c.name (String) категории,
     * [1] = COUNT(v) (Long)
     */
    @Query("""
        SELECT c.name      AS categoryName,
               COUNT(v)     AS cnt
        FROM   Vacancy v
        JOIN   v.categories c
        GROUP  BY c.name
        """)
    List<Object[]> countGroupByCategory();

    /**
     * Возвращает список Object[], где
     * [0] = v.salary (String),
     * [1] = COUNT(v) (Long)
     */
    @Query("""
        SELECT v.salary   AS salaryValue,
               COUNT(v)   AS cnt
        FROM   Vacancy v
        WHERE  v.salary IS NOT NULL
        GROUP  BY v.salary
        """)
    List<Object[]> countGroupBySalary();
}
