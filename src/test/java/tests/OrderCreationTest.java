package tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import api.ApiConfig;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.*;
import api.ApiOrder;
import api.ApiUser;
import models.Order;
import models.User;
import utils.DataGenerator;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Создание заказа") //компоненты
public class OrderCreationTest {
    private User user;
    private String accessToken;
    private static final String VALID_INGREDIENT = "61c0c5a71d1f82001bdaaa72"; // Пример валидного ингредиента
    private static final String INVALID_INGREDIENT = "invalid_hash_123";

    @BeforeClass
    public static void checkApi() {
        ApiConfig.init();
        assumeTrue("API недоступен", ApiConfig.checkApiAvailable());
    }

    @Before
    public void setUp() {
        // Создание и регистрация пользователя
        user = DataGenerator.generateUser();
        Response registerResponse = ApiUser.createUser(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Response deleteResponse = ApiUser.deleteUser(accessToken);

            //проверка, что профиль удалился
            assertEquals("Статус код должен быть 202",
                    SC_ACCEPTED, deleteResponse.statusCode());

            JsonPath json = deleteResponse.jsonPath();
            assertTrue("Флаг успеха должен быть true", json.getBoolean("success"));
            assertEquals("Сообщение должно быть корректным",
                    "User successfully removed",
                    json.getString("message"));
        }
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа для авторизованного пользователя с валидными ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        Order order = new Order(new String[]{VALID_INGREDIENT});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat("Статус код должен быть 200 OK",
                response.statusCode(),
                equalTo(HttpStatus.SC_OK));

        JsonPath json = response.jsonPath();
        assertThat("Флаг успеха должен быть true", json.getBoolean("success"), is(true));
        assertThat("Номер заказа должен быть положительным числом",
                json.getInt("order.number"),
                greaterThan(0));
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа без авторизации") //ошибка в этом тесте, так как возвращает 200, вместо 401
    @Description("Проверка обработки попытки создания заказа без токена авторизации")
    public void testCreateOrderWithoutAuth() {
        Order order = new Order(new String[]{VALID_INGREDIENT});

        Response response = ApiOrder.createOrderWithoutAuth(order);

        assertThat("Статус код должен быть 401 Unauthorized",
                response.statusCode(),
                equalTo(HttpStatus.SC_UNAUTHORIZED));
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка обработки попытки создания заказа без указания ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(new String[]{});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat("Статус код должен быть 400 Bad Request",
                response.statusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST));

        assertThat("Сообщение об ошибке должно быть корректным",
                response.jsonPath().getString("message"),
                equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с невалидным хешем ингредиента")
    @Description("Проверка обработки попытки создания заказа с несуществующим хешем ингредиента")
    public void testCreateOrderWithInvalidIngredient() {
        Order order = new Order(new String[]{INVALID_INGREDIENT});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat("Статус код должен быть 500 Internal Server Error",
                response.statusCode(),
                equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с авторизацией и несколькими ингредиентами")
    @Description("Проверка успешного создания заказа с несколькими валидными ингредиентами")
    public void testCreateOrderWithMultipleIngredients() {
        Order order = new Order(new String[]{VALID_INGREDIENT, "61c0c5a71d1f82001bdaaa73"});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat("Статус код должен быть 200 OK",
                response.statusCode(),
                equalTo(HttpStatus.SC_OK));

        assertThat("Название заказа должно содержать 'бургер'",
                response.jsonPath().getString("name"),
                containsString("бургер"));
    }
}