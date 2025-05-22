package tests;

import org.junit.Test;
import net.datafaker.Faker;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.*;
import api.ApiGetOrder;
import api.ApiOrder;
import api.ApiUser;
import api.ApiConfig;
import models.Order;
import models.User;
import utils.DataGenerator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeTrue;


@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Получение заказов пользователя") //компоненты
public class GetUserOrdersTest {
    private User user;
    private String accessToken;
    private final Faker faker = new Faker();
    private static final String VALID_INGREDIENT = "61c0c5a71d1f82001bdaaa72";

    @BeforeClass
    public static void checkApi() {
        ApiConfig.init();
        assumeTrue("API недоступен", ApiConfig.checkApiAvailable());
    }

    @Before
    public void setUp() {
        // Регистрация пользователя и создание заказа
        user = DataGenerator.generateUser();
        Response registerResponse = ApiUser.createUser(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");

        // Создание тестового заказа
        Order order = new Order(new String[]{VALID_INGREDIENT});
        ApiOrder.createOrder(order, accessToken);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            ApiUser.deleteUser(accessToken);
        }
    }

    @Test
    @Story("Получение заказов")
    @Step("Получение заказов авторизованного пользователя")
    public void testGetOrdersWithAuth() {
        Response response = ApiGetOrder.getUserOrdersWithAuth(accessToken);

        // Проверки
        assertThat(response.statusCode(), equalTo(HttpStatus.SC_OK));
        JsonPath json = response.jsonPath();

        assertThat(json.getBoolean("success"), is(true));
        assertThat(json.getList("orders"), not(empty()));
        assertThat(json.getInt("total"), greaterThan(0));
    }

    @Test
    @Story("Получение заказов")
    @Step("Получение заказов без авторизации")
    public void testGetOrdersWithoutAuth() {
        Response response = ApiGetOrder.getUserOrdersWithoutAuth();

        // Проверки
        assertThat(response.statusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat(response.jsonPath().getString("message"),
                equalTo("You should be authorised"));
    }
}
