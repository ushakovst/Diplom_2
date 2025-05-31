package tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import api.ApiConfig;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import api.ApiUser;
import models.User;
import utils.DataGenerator;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Создание пользователя") //компоненты
public class UserTest {
    private User testUser;
    private String accessToken;

    @BeforeClass
    public static void checkApi() {
        ApiConfig.init();
        assumeTrue("API недоступен", ApiConfig.checkApiAvailable());
    }

    @Before
    public void setUp() {
        testUser = DataGenerator.generateUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Response deleteResponse = ApiUser.deleteUser(accessToken);

            //проверка, что профиль удалился
            assertEquals("Статус код должен быть 202",
                    SC_ACCEPTED,
                    deleteResponse.statusCode());

            JsonPath json = deleteResponse.jsonPath();
            assertTrue("Флаг успеха должен быть true", json.getBoolean("success"));
            assertEquals("Сообщение должно быть корректным",
                    "User successfully removed",
                    json.getString("message"));
        }
    }

    @Test
    @Story("Создание пользователя")
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешной регистрации нового пользователя с валидными данными")
    public void testCreateUserSuccess() {
        Response response = ApiUser.createUser(testUser);

        assertEquals("Статус код должен быть 200 OK",
                HttpStatus.SC_OK,
                response.statusCode());

        JsonPath json = response.jsonPath();
        assertTrue("Флаг успеха должен быть true", json.getBoolean("success"));
        assertNotNull("Токен доступа должен быть получен", json.getString("accessToken"));

        // сохраняем токен для удаления
        this.accessToken = json.getString("accessToken");
    }

    @Test
    @Story("Создание пользователя")
    @DisplayName("Создание дубликата пользователя")
    @Description("Проверка обработки попытки регистрации уже существующего пользователя")
    public void testCreateDuplicateUser() {
        Response firstResponse = ApiUser.createUser(testUser);
        this.accessToken = firstResponse.jsonPath().getString("accessToken"); // Сохраняем токен

        // Проверка успешности создания первого пользователя
        assertEquals("Статус код первого пользователя должен быть 200 OK",
                HttpStatus.SC_OK,
                firstResponse.statusCode());

        // Пытаемся создать дубликат
        Response response = ApiUser.createUser(testUser);

        assertEquals("Статус код должен быть 403 Forbidden",
                HttpStatus.SC_FORBIDDEN,
                response.statusCode());

        assertEquals("Сообщение об ошибке должно быть корректным",
                "User already exists",
                response.jsonPath().getString("message"));
    }

    @Test
    @Story("Создание пользователя")
    @DisplayName("Создание пользователя без обязательного поля")
    @Description("Проверка регистрации пользователя без обязательного поля в запросе: email")
    public void testCreateUserWithoutEmail() {
        testUser = DataGenerator.generateUserWithoutEmail();
        Response response = ApiUser.createUserWithoutEmail(testUser);

        assertEquals("Статус код должен быть 403 Forbidden",
                HttpStatus.SC_FORBIDDEN,
                response.statusCode());

        JsonPath json = response.jsonPath();
        assertFalse("Флаг успеха должен быть false",
                json.getBoolean("success"));

        assertEquals("Сообщение об ошибке должно быть корректным",
                "Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    @Story("Создание пользователя")
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка регистрации пользователя без обязательного поля: password")
    public void testCreateUserWithoutPassword() {
        testUser = DataGenerator.generateUserWithoutPassword();
        Response response = ApiUser.createUser(testUser);

        assertEquals("Статус код должен быть 403 Forbidden",
                HttpStatus.SC_FORBIDDEN,
                response.statusCode());

        JsonPath json = response.jsonPath();
        assertFalse("Флаг успеха должен быть false", json.getBoolean("success"));
        assertEquals("Сообщение об ошибке должно быть корректным",
                "Email, password and name are required fields",
                json.getString("message"));
    }

    @Test
    @Story("Создание пользователя")
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка регистрации пользователя без обязательного поля: name")
    public void testCreateUserWithoutName() {
        testUser = DataGenerator.generateUserWithoutName();
        Response response = ApiUser.createUser(testUser);

        assertEquals("Статус код должен быть 403 Forbidden",
                HttpStatus.SC_FORBIDDEN,
                response.statusCode());

        JsonPath json = response.jsonPath();
        assertFalse("Флаг успеха должен быть false", json.getBoolean("success"));
        assertEquals("Сообщение об ошибке должно быть корректным",
                "Email, password and name are required fields",
                json.getString("message"));
    }
}