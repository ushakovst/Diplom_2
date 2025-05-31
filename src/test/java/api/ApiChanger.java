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
                .contentType("application/json")
                .body(user) // просто передает объект
                .patch(USER);
    }
//(gson.toJson(user))
//.header("Content-type", "application/json")
    @Step("Обновление данных пользователя без авторизации")
    public static Response updateUserWithoutAuth(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .patch(USER);
    }
}
