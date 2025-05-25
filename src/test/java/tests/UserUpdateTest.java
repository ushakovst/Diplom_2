package tests;

import org.junit.Test;
import api.ApiConfig;
import net.datafaker.Faker;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.*;
import api.ApiChanger;
import api.ApiUser;
import models.User;
import utils.DataGenerator;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;


@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Изменения данных пользователя") //компоненты
public class UserUpdateTest {
    private User originalUser;
    private String accessToken;
    private final Faker faker = new Faker();

    @BeforeClass
    public static void checkApi() {
        ApiConfig.init();
        assumeTrue("API недоступен", ApiConfig.checkApiAvailable());
    }

    @Before
    public void setUp() {
        // Создаем и регистрируем пользователя
        originalUser = DataGenerator.generateUser();
        Response registerResponse = ApiUser.createUser(originalUser);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
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
    @Story("Изменение данных пользователя")
    @Step("Успешное обновление данных с авторизацией")
    public void testUpdateUserWithAuth() {
        // Генерируем новые данные
        User updatedUser = User.builder()
                .email(faker.internet().emailAddress())
                .name(faker.name().fullName())
                .build();

        // Отправляем запрос на обновление
        Response response = ApiChanger.updateUser(updatedUser, accessToken);

        // Проверки ответа
        assertEquals(HttpStatus.SC_OK, response.statusCode());

        JsonPath json = response.jsonPath();
        assertThat(json.getBoolean("success"), is(true));
        assertThat(json.getString("user.email"), equalTo(updatedUser.getEmail()));
        assertThat(json.getString("user.name"), equalTo(updatedUser.getName()));
    }

    @Test
    @Story("Изменение данных пользователя")
    @Step("Обновление данных без авторизации")
    public void testUpdateUserWithoutAuth() {
        User updatedUser = User.builder()
                .email(faker.internet().emailAddress())
                .name(faker.name().fullName())
                .build();

        Response response = ApiChanger.updateUserWithoutAuth(updatedUser);

        // Проверки
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
        assertThat(response.jsonPath().getString("message"),
                equalTo("You should be authorised"));
    }

    @Test
    @Story("Изменение данных пользователя")
    @Step("Обновление пароля с авторизацией")
    public void testUpdatePasswordWithAuth() {
        User updatedUser = User.builder()
                .password(faker.internet().password(10, 16))
                .build();

        Response response = ApiChanger.updateUser(updatedUser, accessToken);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertThat(response.jsonPath().getBoolean("success"), is(true));
    }
}