ALTER TABLE vacancy
    ADD CONSTRAINT uq_vacancy_url UNIQUE (url);