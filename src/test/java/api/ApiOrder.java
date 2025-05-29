package api;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;
import static endpoints.Endpoints.ORDERS;

public class ApiOrder {
    private static final Gson gson = new Gson();

    @Step("Создание заказа")
    public static Response createOrder(Order order, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .contentType("application/json")
                .body(order)
                .post(ORDERS);
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutAuth(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .post(ORDERS);
    }
}