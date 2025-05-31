package tests;

import io.qameta.allure.junit4.DisplayName;
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

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;


@Epic("API тесты для Stellar Burgers.") //раздел системы.
@Feature("Получение заказов пользователя") //компоненты
public class GetUserOrdersTest {
    private User user;
    private String accessToken;
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

        // Извлекаем токен (убедитесь, что поле "accessToken" существует в ответе)
        this.accessToken = registerResponse.jsonPath().getString("accessToken");
        assertNotNull("Токен не получен", accessToken);

        // Создание тестового заказа
        Order order = new Order(new String[]{VALID_INGREDIENT});
        ApiOrder.createOrder(order, accessToken);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Response deleteResponse = ApiUser.deleteUser(accessToken);

            // Проверка ответа
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
    @Story("Получение заказов")
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка успешного получения списка заказов для авторизованного пользователя")
    public void testGetOrdersWithAuth() {
        Response response = ApiGetOrder.getUserOrdersWithAuth(accessToken);

        // Проверки
        assertThat("Статус код должен быть 200 OK",
                response.statusCode(),
                equalTo(HttpStatus.SC_OK));

        JsonPath json = response.jsonPath();

        assertThat("Флаг успеха должен быть true", json.getBoolean("success"), is(true));
        assertThat("Список заказов не должен быть пустым", json.getList("orders"), not(empty()));
        assertThat("Общее количество заказов должно быть больше 0", json.getInt("total"), greaterThan(0));
    }

    @Test
    @Story("Получение заказов")
    @DisplayName("Получение заказов")
    @Description("Проверка обработки запроса заказов без токена авторизации")
    public void testGetOrdersWithoutAuth() {
        Response response = ApiGetOrder.getUserOrdersWithoutAuth();

        // Проверки
        assertThat("Статус код должен быть 401 Unauthorized",
                response.statusCode(),
                equalTo(HttpStatus.SC_UNAUTHORIZED));

        assertThat("Сообщение об ошибке должно быть корректным",
                response.jsonPath().getString("message"),
                equalTo("You should be authorised"));
    }
}
