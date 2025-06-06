# Diplom_2
Проект содержит автотесты API учебного сервиса [Stellar Burgers](https://stellarburgers.nomoreparties.site/). Его [документация](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf).

## Требования к проекту
1. Должны быть протестированы следующие ручки:

   1.1. Создание пользователя:
      - создать уникального пользователя,
      - создать пользователя, который уже зарегистрирован,
      - создать пользователя и не заполнить одно из обязательных полей.
      
   1.2. Логин пользователя:
      - логин под существующим пользователем,
      - логин с неверным логином и паролем.
     
   1.3. Изменение данных пользователя:
      - с авторизацией,
      - без авторизации.
      
   1.4. Создание заказа:
      - с авторизацией,
      - без авторизации,
      - с ингредиентами,
      - без ингредиентов,
      - с неверным хешем ингредиентов.
      
   1.5. Получение заказов конкретного пользователя:
      - авторизованный пользователь,
      - неавторизованный пользователь.
   
2. Для каждой ручки тесты лежат в отдельном классе.
3. В тестах проверяется тело и код ответа.
4. Все тесты независимы.

## Технологии, использующиеся в проекте
1. Java 11
2. Maven 2.22.2
3. JUnit 4.13.2
4. RestAssured 4.5.1
5. Allure 2.17.3
6. Lombok 1.18.30 (так же необходимо, чтобы Lombok плагин был установлен в вашей IDE. Для IntelliJ IDEA: Settings → Plugins → Lombok)

## Запуск тестов
Чтобы запустить тесты, выполните следующую команду в терминале:
```bash
mvn clean compile
mvn clean test
```

## Для создания отчета в Allure
```bash
mvn allure:report
mvn allure:serve
```