# Weather Viewer

Weather Viewer — учебное веб-приложение для просмотра текущей погоды. Пользователь может зарегистрироваться, войти в аккаунт, найти локацию через OpenWeather API, добавить ее в свой список и видеть погоду по сохраненным локациям на главной странице.

## Стек

- Java 21
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Liquibase
- Thymeleaf
- Bootstrap 5
- Gradle
- OpenWeather API
- Caffeine Cache
- JUnit 5
- WireMock
- BCrypt

## Возможности

- Регистрация и авторизация пользователя
- Logout
- Ручная работа с cookies и sessions без `JSESSIONID`
- Хранение sessions в базе данных
- Очистка истекших sessions через scheduler
- Поиск локаций через OpenWeather API
- Добавление и удаление сохраненных локаций
- Отображение текущей погоды по сохраненным локациям
- Кэширование запросов к OpenWeather API
- Интеграционные тесты сервисов и API-клиента

## Структура проекта

```text
ru.prplhd.weather
├── client          # работа с OpenWeather API
├── config          # Spring, JPA, DataSource, Liquibase, beans
├── dto             # DTO для auth, API и view-слоя
├── exception       # пользовательские исключения
├── mapper          # преобразование DTO и entity
├── persistence     # JPA entities и repositories
├── scheduler       # фоновая задача
├── service         # бизнес-логика
├── validation      # кастомная валидация
└── web             # controllers, interceptors, cookies/session resolving,
                      model advice, exception handlers
```

```text
src/main/resources
├── app.properties      # настройки приложения
└── db/changelog        # Liquibase migrations
```

```text
src/main/webapp
├── css                 # стили
├── images              # изображения
└── WEB-INF             # Thymeleaf-шаблоны
```

## Кэширование

Для кэширования используется Caffeine Cache. Кэшируются результаты поиска локаций (на 24 часа) и данные текущей погоды (на 5 минут). Это уменьшает количество запросов к OpenWeather API и ускоряет повторные обращения.

## Локальный запуск

### Требования

- JDK 21
- PostgreSQL
- Tomcat 11
- OpenWeather API key (можно получить бесплатно после регистрации на https://openweathermap.org/)

### Переменные окружения

Для локального запуска нужно задать следующие environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/weather_dev
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
API_KEY=your_openweather_api_key
```

### База данных

Перед запуском нужно вручную создать базу данных `weather_dev` и две схемы внутри нее:

```sql
CREATE DATABASE weather_dev;
```

```sql
CREATE SCHEMA app;
CREATE SCHEMA test;
```

Схема `app` используется для запуска приложения, схема `test` — для тестов.

### Сборка

Собрать проект можно через Gradle Wrapper:

```bash
gradlew clean build
```

Готовый WAR-файл появится в:

```text
build/libs
```

### Деплой

Скопировать WAR-файл в директорию Tomcat:

```text
$TOMCAT_HOME/webapps
```

Если файл переименовать в `ROOT.war`, приложение будет доступно по адресу:

```text
http://localhost:8080/
```

## Тесты

Запуск всех тестов:

```bash
gradlew test
```

В проекте также настроены отдельные Gradle-таски для запуска тестов по тегам:

```bash
gradlew authTest
gradlew locationWeatherTest
gradlew httpServerTest
```

В тестах используются JUnit 5, AssertJ, Mockito и WireMock. Запросы к реальному OpenWeather API во время тестов не выполняются.

## Известные недочеты

### Функциональные недочеты

- Главная страница доступна только авторизованным пользователям, хотя по ТЗ она также должна быть доступна гостям, сделал немного по своему
- Страницы регистрации и авторизации остаются доступными даже для уже авторизованного пользователя, уже не стал исправлять, все что нужно было от проекта получил

### Технические недочеты

- Hidden inputs при сохранении локации отдельно не валидируются на сервере
- Часть числовых значений захардкожена. Это сделано осознанно, так как практика с вынесением в `properties` уже была отработана в других частях проекта, просто было лень уже, да и данные значения я менять не собирался

***

# Спасибо за внимание к моему проекту!