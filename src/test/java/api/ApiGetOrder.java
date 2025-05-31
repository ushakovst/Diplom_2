package api;

import endpoints.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiGetOrder {
    @Step("Получение заказа с авторизацией")
    public static Response getUserOrdersWithAuth(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .get(Endpoints.ORDERS);
    }

    @Step("Получение заказа без авторизации")
    public static Response getUserOrdersWithoutAuth() {
        return given()
                .get(Endpoints.ORDERS);
    }
}