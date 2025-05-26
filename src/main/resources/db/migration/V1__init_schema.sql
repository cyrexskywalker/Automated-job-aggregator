-- Источники вакансий
CREATE TABLE source (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    base_url TEXT NOT NULL
);

-- Компании
CREATE TABLE company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Вакансии
CREATE TABLE vacancy (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES source(id),
    company_id BIGINT NOT NULL REFERENCES company(id),
    title VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    salary TEXT,
    description TEXT,
    publication_date TIMESTAMP,
    url TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Категории
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Связь многие-ко-многим вакансии ↔ категории
CREATE TABLE vacancy_category (
    vacancy_id  BIGINT NOT NULL REFERENCES vacancy(id),
    category_id BIGINT NOT NULL REFERENCES category(id),
    PRIMARY KEY (vacancy_id, category_id)
);

-- Подписки пользователей
CREATE TABLE user_subscription (
    id BIGSERIAL PRIMARY KEY,
    criteria TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
