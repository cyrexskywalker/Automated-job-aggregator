package com.javaproject.vacancy_aggregator.specification;

import com.javaproject.vacancy_aggregator.domain.Vacancy;
import org.springframework.data.jpa.domain.Specification;

public class VacancySpecifications {
    public static Specification<Vacancy> cityEquals(String city) {
        return (root, cq, cb) ->
                cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }



    public static Specification<Vacancy> companyEquals(String company) {
        return (root, cq, cb) ->
                cb.equal(cb.lower(root.get("company").get("name")), company.toLowerCase());
    }

    public static Specification<Vacancy> salaryContains(String part) {
        return (root, cq, cb) ->
                cb.like(cb.lower(root.get("salary")), "%" + part.toLowerCase() + "%");
    }

    public static Specification<Vacancy> salaryMinGreaterThanEqual(Integer minSalary) {
        return (root, cq, cb) ->
                cb.greaterThanOrEqualTo(root.get("salaryMin"), minSalary);
    }

    public static Specification<Vacancy> salaryMaxLessThanEqual(Integer maxSalary) {
        return (root, cq, cb) ->
                cb.lessThanOrEqualTo(root.get("salaryMax"), maxSalary);
    }

    public static Specification<Vacancy> salaryBetween(Integer minSalary, Integer maxSalary) {
        return (root, cq, cb) -> {
            if (minSalary != null && maxSalary != null) {
                return cb.between(root.get("salary"), minSalary, maxSalary);
            } else if (minSalary != null) {
                return cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
            } else if (maxSalary != null) {
                return cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
            }
            return null;
        };
    }

    public static Specification<Vacancy> employmentEquals(String employmentType) {
        return (root, cq, cb) ->
                cb.equal(cb.lower(root.get("employmentType")), employmentType.toLowerCase());
    }

    public static Specification<Vacancy> textContainsKeyword(String keyword) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        return (root, cq, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                );
    }
}