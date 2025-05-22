package api;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import java.util.List;

import static io.restassured.RestAssured.given;
import static endpoints.Endpoints.ORDERS;

public class ApiOrder {
    private static final Gson gson = new Gson();

    @Step("Создание заказа")
    public static Response createOrder(Order order, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(gson.toJson(order))
                .post(ORDERS);
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(gson.toJson(order))
                .post(ORDERS);
    }
}