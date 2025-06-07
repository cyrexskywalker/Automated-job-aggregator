package com.javaproject.vacancy_aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class VacancyAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VacancyAggregatorApplication.class, args);
	}
}
