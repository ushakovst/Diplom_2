package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeTrue;

public class ApiConfig {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    private ApiConfig() {
    }

    @Step("Инициализация URL")
    public static void init() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
    }

    @Step("Проверка доступности API")
    public static boolean checkApiAvailable() {
        try {
            given()
                    .baseUri(BASE_URL)
                    .when()
                    .get("/api")
                    .then()
                    .statusCode(anyOf(is(SC_OK), is(SC_NOT_FOUND)));
            return true;
        } catch (Exception e) {
            assumeTrue("API недоступен: " + e.getMessage(), false);
            return false;
        }
    }
}