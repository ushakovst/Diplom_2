package tests;

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
            assertEquals(SC_ACCEPTED, deleteResponse.statusCode());
            JsonPath json = deleteResponse.jsonPath();
            assertTrue(json.getBoolean("success"));
            assertEquals("User successfully removed", json.getString("message"));
        }
    }

    @Test
    @Story("Создание пользователя")
    @Step("Создание уникального пользователя")
    public void testCreateUserSuccess() {
        Response response = ApiUser.createUser(testUser);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        JsonPath json = response.jsonPath();
        assertTrue(json.getBoolean("success"));
        assertNotNull(json.getString("accessToken"));
    }

    @Test
    @Story("Создание пользователя")
    @Step("Создание дубликата пользователя")
    public void testCreateDuplicateUser() {
        Response firstResponse = ApiUser.createUser(testUser);
        this.accessToken = firstResponse.jsonPath().getString("accessToken"); // Сохраняем токен

        // Пытаемся создать дубликат
        Response response = ApiUser.createUser(testUser);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertEquals("User already exists", response.jsonPath().getString("message"));
    }
}