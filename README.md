# Vacancy Aggregator

![Spring Boot 3.4.5](https://img.shields.io/badge/Spring%20Boot-3.4.5-green)  
![Java 21](https://img.shields.io/badge/Java-21-blue)  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.5-blue)

Сервис для агрегации вакансий из разных источников с возможностью фильтрации, аналитики и уведомлений на основе пользовательских критериев.

---

## 📋 Содержание

1. [Описание](#-описание)
2. [Функциональность](#-функциональность)
3. [Технологии](#-технологии)
4. [Установка и запуск](#-установка-и-запуск)
5. [API Endpoints](#-api-endpoints)
6. [Документация](#-документация)

---

## 📖 Описание

**Vacancy Aggregator** — Spring Boot-приложение, которое:

- Собирает вакансии (через сервисный слой) и сохраняет их в PostgreSQL
- Предоставляет REST-API для поиска и фильтрации вакансий
- Позволяет пользователям задавать критерии уведомлений о новых вакансиях
- Отображает аналитику по количеству вакансий в разрезе городов, категорий и диапазонов зарплат

---

## 🚀 Функциональность

- **Vacancies API**: поиск & фильтры (город, компания, зарплата, ключевое слово), пагинация, сортировка
- **Notifications API**: CRUD для критериев уведомлений
- **Analytics API**: группировка вакансий по городам, категориям и зарплатным диапазонам

---

## 🛠 Технологии

- Java 21
- Spring Boot 3.4.5 (Web, Data JPA, Validation)
- PostgreSQL (42.x драйвер)
- Flyway 10.x — миграции БД
- JUnit 5 + Mockito — тестирование
- Maven 3.8+

---

## 🎯 Установка и запуск

1. **Клонировать репозиторий**
   ```bash
   git clone https://github.com/cyrexskywalker/vacancy-aggregator.git
   cd vacancy-aggregator
   ```
2.	**Создать базы данных.
      Выполните в psql или GUI:**
      ```bash
       CREATE DATABASE vacancies_dev;
       CREATE DATABASE vacancies_test;
      ```
3.	**Настроить application.yml.**
     В src/main/resources/application.yml:
       ```yaml
      spring.datasource.url=jdbc:postgresql://localhost:5432/vacancies_dev
      spring.datasource.username=your_db_user
      spring.datasource.password=your_db_password
      spring.jpa.hibernate.ddl-auto=validate
      spring.jpa.show-sql=true
      spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
      spring.flyway.enabled=true
      spring.flyway.locations=classpath:db/migration
          
      spring.config.activate.on-profile=test
      spring.datasource.url=jdbc:postgresql://localhost:5432/vacancies_test
      spring.datasource.username=your_db_user
      spring.datasource.password=your_db_password
      spring.flyway.enabled=false
      ```
4. **Сборка и запуск**
    ```bash
    mvn clean package
    mvn spring-boot:run
    ```
5.	**Запуск тестов**
      ```bash
      mvn test
      ```

## 📡 API Endpoints

### 🎯 Vacancies

- **GET** `/api/vacancies`
    - **Описание:** Получить постраничный список вакансий с дополнительными фильтрами.
    - **Query Parameters:**
        - `city` (string, optional) — filter by city
        - `company` (string, optional) — filter by company name
        - `salary` (string, optional) — minimum salary threshold
        - `keyword` (string, optional) — search keyword in title or description
        - `page` (integer, optional, default `0`) — page index (0-based)
        - `size` (integer, optional, default `50`) — page size
        - `sort` (string, optional, e.g. `publicationDate,desc`)
    - **Response (200 OK):**
      ```json
      {
        "content": [
          {
            "id": 42,
            "title": "Java Developer",
            "company": { "name": "ACME Corp" },
            "city": "Москва",
            "salary": "120000",
            "publicationDate": "2025-06-05T10:00:00",
            "url": "https://example.com/vacancy/42",
            "description": "Ищем Java-разработчика"
          }
        ],
        "pageable": {},
        "totalElements": 123,
        "totalPages": 3
      }
      ```

### 🔔 Notifications

- **POST** `/api/notifications`
    - **Описание:** Создать новое уведомление.
    - **Request Body:**
      ```json
      {
        "city": "Москва",
        "keyword": "Java",
        "minSalary": "100000",
        "frequency": "DAILY"
      }
      ```
    - **Response (200 OK):**
      ```json
      {
        "id": 7,
        "city": "Москва",
        "keyword": "Java",
        "minSalary": "100000",
        "createdAt": "2025-06-07T12:34:56"
      }
      ```

- **GET** `/api/notifications`
    - **Описание:** Список со всеми уведомлениями.
    - **Response (200 OK):**
      ```json
      [
        {
          "id": 7,
          "city": "Москва",
          "keyword": "Java",
          "minSalary": "100000"
        },
        {
          "id": 8,
          "city": "Санкт-Петербург",
          "keyword": "Python",
          "minSalary": "90000"
        }
      ]
      ```

- **PUT** `/api/notifications/{id}`
    - **Описание:** Обновить существующие критерии уведомления.
    - **Path Parameter:** `id` (integer) — ID критерия для обновления
    - **Request Body:** такой же, как и **POST** выше
    - **Response (200 OK):** обновленный объект критериев

- **DELETE** `/api/notifications/{id}`
    - **Описание:** Удалить уведомление по ID.
    - **Path Parameter:** `id` (integer)
    - **Response:** 204 No Content

### 📊 Analytics

- **GET** `/api/analytics/count-by-city`
    - **Описание:** Получить количество вакансий, сгруппированных по городам.
    - **Response (200 OK):**
      ```json
      [
        { "city": "Москва", "count": 50 },
        { "city": "Санкт-Петербург", "count": 30 }
      ]
      ```

- **GET** `/api/analytics/count-by-category`
    - **Описание:** Получить количество вакансий, сгруппированных по категориям.
    - **Response (200 OK):**
      ```json
      [
        { "category": "Development", "count": 70 },
        { "category": "Design", "count": 20 }
      ]
      ```

- **GET** `/api/analytics/count-by-salary`
    - **Описание:** Получить количество вакансий, сгруппированных по зарплатам.
    - **Response (200 OK):**
      ```json
      [
        { "salaryRange": "0-50000", "count": 10 },
        { "salaryRange": "50000-100000", "count": 60 }
      ]
      ```

## 📚 Документация

Для удобства разработки и интеграции с сервисом реализована автоматическая документация API в формате OpenAPI/Swagger.

### Swagger UI

После запуска приложения перейдите в браузере по адресу: http://localhost:8080/swagger-ui/index.html
— здесь вы увидите все доступные эндпоинты, их параметры и модели запросов/ответов.

### OpenAPI JSON/YAML

Если нужно получить «сырую» спецификацию OpenAPI, можно воспользоваться:

- JSON:  http://localhost:8080/v3/api-docs
- YAML:  http://localhost:8080/v3/api-docs.yaml
### Генерация клиентских SDK

Из спецификации OpenAPI можно автоматически сгенерировать клиентские библиотеки на разных языках. Например, с помощью [OpenAPI Generator](https://openapi-generator.tech/) :

```bash
    openapi-generator-cli generate \
    -i http://localhost:8080/v3/api-docs \
    -g java \
    -o ./client-sdk-java
   ```


