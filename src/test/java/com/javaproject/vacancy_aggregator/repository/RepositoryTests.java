package com.javaproject.vacancy_aggregator.repository;

import com.javaproject.vacancy_aggregator.domain.Source;
import com.javaproject.vacancy_aggregator.domain.Company;
import com.javaproject.vacancy_aggregator.domain.Category;
import com.javaproject.vacancy_aggregator.domain.Vacancy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class RepositoryTests {
    @Autowired
    private SourceRepository sourceRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private VacancyRepository vacancyRepo;

    @Test
    @DisplayName("SourceRepository saves and finds by name")
    void sourceRepositorySaveAndFind() {
        Source src = new Source("test-source", "http://example.com");
        sourceRepo.save(src);

        Optional<Source> found = sourceRepo.findByName("test-source");
        assertThat(found).isPresent();
        assertThat(found.get().getBaseUrl()).isEqualTo("http://example.com");
    }

    @Test
    @DisplayName("CompanyRepository saves and finds by name")
    void companyRepositorySaveAndFind() {
        Company comp = new Company("ExampleCo");
        companyRepo.save(comp);

        Optional<Company> found = companyRepo.findByName("ExampleCo");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ExampleCo");
    }

    @Test
    @DisplayName("CategoryRepository saves and finds by name")
    void categoryRepositorySaveAndFind() {
        Category cat = new Category("IT");
        categoryRepo.save(cat);

        Optional<Category> found = categoryRepo.findByName("IT");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("IT");
    }

    @Test
    @DisplayName("VacancyRepository existsByUrl and save")
    void vacancyRepositoryExistsByUrl() {
        Vacancy v = new Vacancy();
        v.setTitle("Dev");
        v.setUrl("http://example.com/vac/1");
        // Need to set mandatory associations: source and company
        Source src = new Source("src", "u");
        Company comp = new Company("comp");
        // save dependencies so foreign keys valid
        sourceRepo.save(src);
        companyRepo.save(comp);
        v.setSource(src);
        v.setCompany(comp);

        vacancyRepo.save(v);

        boolean exists = vacancyRepo.existsByUrl("http://example.com/vac/1");
        assertThat(exists).isTrue();

        boolean notExists = vacancyRepo.existsByUrl("bad-url");
        assertThat(notExists).isFalse();
    }
}
