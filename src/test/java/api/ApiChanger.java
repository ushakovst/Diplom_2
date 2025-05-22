package api;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;
import static io.restassured.RestAssured.given;
import static endpoints.Endpoints.USER;

public class ApiChanger {
    private static final Gson gson = new Gson();

    @Step("Обновление данных пользователя")
    public static Response updateUser(User user, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(gson.toJson(user))
                .patch(USER);
    }

    @Step("Обновление данных пользователя без авторизации")
    public static Response updateUserWithoutAuth(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(gson.toJson(user))
                .patch(USER);
    }
}
