package tests;

import org.junit.Test;
import api.ApiConfig;
import net.datafaker.Faker;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeTrue;

@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Создание заказа") //компоненты
public class OrderCreationTest {
    private User user;
    private String accessToken;
    private final Faker faker = new Faker();
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
            ApiUser.deleteUser(accessToken);
        }
    }

    @Test
    @Story("Создание заказа")
    @Step("Создание заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        Order order = new Order(new String[]{VALID_INGREDIENT});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat(response.statusCode(), equalTo(HttpStatus.SC_OK));
        JsonPath json = response.jsonPath();
        assertThat(json.getBoolean("success"), is(true));
        assertThat(json.getInt("order.number"), greaterThan(0));
    }

    @Test
    @Story("Создание заказа")
    @Step("Создание заказа без авторизации") //ошибка в этом тесте, так как возвращает 200, вместо 401
    public void testCreateOrderWithoutAuth() {
        Order order = new Order(new String[]{VALID_INGREDIENT});

        Response response = ApiOrder.createOrderWithoutAuth(order);

        assertThat(response.statusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
    }

    @Test
    @Story("Создание заказа")
    @Step("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(new String[]{});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat(response.statusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.jsonPath().getString("message"),
                equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Story("Создание заказа")
    @Step("Создание заказа с невалидным хешем ингредиента")
    public void testCreateOrderWithInvalidIngredient() {
        Order order = new Order(new String[]{INVALID_INGREDIENT});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat(response.statusCode(), equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @Story("Создание заказа")
    @Step("Создание заказа с авторизацией и несколькими ингредиентами")
    public void testCreateOrderWithMultipleIngredients() {
        Order order = new Order(new String[]{VALID_INGREDIENT, "61c0c5a71d1f82001bdaaa73"});

        Response response = ApiOrder.createOrder(order, accessToken);

        assertThat(response.statusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(response.jsonPath().getString("name"), containsString("бургер"));
    }
}