package api;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;
import static io.restassured.RestAssured.given;
import static endpoints.Endpoints.*;

public class ApiUser {
    private static final Gson gson = new Gson();

    @Step("Создание пользователя")
    public static Response createUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .post(REGISTER);
    }

    @Step("Создание пользователя без поля email")
    public static Response createUserWithoutEmail(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .post(REGISTER);
    }

    @Step("Авторизация пользователя")
    public static Response loginUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .post(LOGIN);
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .delete(USER);
    }
}