package com.javaproject.vacancy_aggregator;

import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.repository.SourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {
    private final SourceRepository sourceRepo;

    public DataInitializer(SourceRepository sourceRepo) {
        this.sourceRepo = sourceRepo;
    }

    @Override
    public void run(String... args) {
        sourceRepo.findByName("hh.ru")
                .or(() -> {
                    sourceRepo.save(new Source("hh.ru", "https://hh.ru"));
                    System.out.println("✔ Добавили источник hh.ru");
                    return Optional.empty();
                });
    }
}
