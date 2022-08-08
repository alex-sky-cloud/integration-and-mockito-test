package wiremock.demo.wiremockjunit5manual;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.HttpsSettings;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;


/**
 * Настройка WireMock вручную
 */
public class WireMockManualJUnit5Test {

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
        setupStub();
    }

    private void setupStub() {

        /*указываем с каким наполнением будет отправлен response
         * от данного сервера-заглушки.
         * Также указываем путь к файлу, который будет помещен в тело
         * response*/
        ResponseDefinitionBuilder responseDefinitionBuilder =
                aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withHeader("rec_count", "3")
                        .withStatus(200)
                        .withBodyFile("json/example.json");

        /*указываем суффикс к адресу, на который будут приниматься запросы.
         * При этом, когда приходит запрос, тогда происходит проверка адреса,
         * на который пришел запрос и если данный адрес для endpoint - не совпадает,
         * тогда будет выброшена ошибка*/
        UrlPattern urlPatternOfSuffixExternalServer = urlEqualTo("/an/endpoint");

        /*после сопоставления адреса полученного запроса, формируем response, который
         * будет отправлять сервер-заглушка*/
        MappingBuilder mappingBuilder = get(urlPatternOfSuffixExternalServer);
        mappingBuilder.willReturn(responseDefinitionBuilder);

        wireMockServer.stubFor(mappingBuilder);
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testStatusCodePositive() {
        given().
                when().
                get("http://localhost:8090/an/endpoint").
                then().
                assertThat().statusCode(200);
    }


    @Test
    public void testStatusCodeNegative() {
        given().
                when().
                get("http://localhost:8090/another/endpoint").
                then().
                assertThat().statusCode(404);
    }

    @Test
    public void testResponseContents() {
        Response response = given()
                .when()
                .get("http://localhost:8090/an/endpoint");

        /*получаем значение из определенного ключа в полученном json,
        в формате строки*/
        JsonPath jsonPath = response.jsonPath();
        List<Map<String,String>> jsonArray = jsonPath.getJsonObject("d.results");

        String discType = jsonArray
                .stream()
                .map(entry -> entry.get("DiscType"))
                .filter(entry -> entry.equals("SSD"))
                .findFirst().orElseThrow(() -> new RuntimeException("Не удалось найти значение по ключу 'DiscType'"));

        Assert.assertEquals("SSD", discType);
    }

}
