package tests;

import org.junit.Test;
import net.datafaker.Faker;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.*;
import api.ApiUser;
import api.ApiConfig;
import models.User;
import utils.DataGenerator;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Авторизация пользователя") //компоненты
public class AuthTest {
    private User validUser;
    private String accessToken;
    private final Faker faker = new Faker();

    @BeforeClass
    public static void checkApi() {
        ApiConfig.init();
        assumeTrue("API недоступен", ApiConfig.checkApiAvailable());
    }

    @Before
    public void setUp() {
        // Создаем пользователя перед тестами
        validUser = DataGenerator.generateUser();
        Response registerResponse = ApiUser.createUser(validUser);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        // Удаляем пользователя после тестов
        if (accessToken != null) {
            Response deleteResponse = ApiUser.deleteUser(accessToken);

            //проверка, что профиль удалился
            assertEquals(SC_ACCEPTED, deleteResponse.statusCode());
            JsonPath json = deleteResponse.jsonPath();
            assertTrue(json.getBoolean("success"));
            assertEquals("User successfully removed", json.getString("message"));
        }
    }

    @Test
    @Story("Авторизация пользователя")
    @Step("Успешный логин под существующим пользователем")
    public void testLoginWithValidCredentials() {
        // Отправляем запрос на авторизацию
        Response loginResponse = ApiUser.loginUser(validUser);

        // Проверяем статус и тело ответа
        assertEquals(HttpStatus.SC_OK, loginResponse.statusCode());

        JsonPath json = loginResponse.jsonPath();
        assertThat(json.getBoolean("success"), is(true));
        assertThat(json.getString("accessToken"), not(emptyString()));
        assertThat(json.getString("refreshToken"), not(emptyString()));
    }

    @Test
    @Story("Авторизация пользователя")
    @Step("Логин с неверным паролем")
    public void testLoginWithInvalidPassword() {
        // Создаем пользователя с неверным паролем
        User invalidUser = User.builder()
                .email(validUser.getEmail())
                .password("wrong_password")
                .build();

        // Отправляем запрос
        Response loginResponse = ApiUser.loginUser(invalidUser);

        // Проверки
        assertEquals(HttpStatus.SC_UNAUTHORIZED, loginResponse.statusCode());
        assertThat(loginResponse.jsonPath().getString("message"),
                equalTo("email or password are incorrect"));
    }

    @Test
    @Story("Авторизация пользователя")
    @Step("Логин с несуществующим email")
    public void testLoginWithNonExistentEmail() {
        // Генерируем случайный несуществующий email
        User invalidUser = User.builder()
                .email(faker.internet().emailAddress())
                .password("any_password")
                .build();

        Response loginResponse = ApiUser.loginUser(invalidUser);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, loginResponse.statusCode());
        assertThat(loginResponse.jsonPath().getString("message"),
                equalTo("email or password are incorrect"));
    }
}