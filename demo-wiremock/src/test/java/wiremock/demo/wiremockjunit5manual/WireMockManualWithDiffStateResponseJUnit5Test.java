package wiremock.demo.wiremockjunit5manual;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;

/**
 * Тестовый класс описывает настройку сервера-заглушки, который может возвращать
 * несколько возможных состояний ответа
 */
public class WireMockManualWithDiffStateResponseJUnit5Test {

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup () {

        WireMockConfiguration mockConfigurationPort = wireMockConfig()
                .port(8089);

        wireMockServer = new WireMockServer(mockConfigurationPort);
        wireMockServer.start();

        /*Это необязательно. Нужно только тогда, когда для тестирования
        * используется RestAssured-библиотека*/
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = 8089;

        setupStub();

    }


    public void setupStub() {

        configureFor("127.0.0.1", 8089);

        setupResponseInCaseFailedProcessingRequest();

        setupResponseInCaseSuccessProcessingRequest();
    }

    /*настройка response в случае успешной обработки запроса*/
    void setupResponseInCaseSuccessProcessingRequest(){

        UrlPattern urlPattern = urlEqualTo("/some/thing");
        MappingBuilder mappingBuilder = get(urlPattern);

        MappingBuilder mappingBuilderWithHeader = mappingBuilder
                .withHeader("Accept", matching("application/json"));

        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse().
                withStatus(200).
                withHeader("Content-Type", "application/json")
                .withBody("{\"serviceStatus\": \"running\"}")
                .withFixedDelay(2500);

        MappingBuilder responseForReturn = mappingBuilderWithHeader.willReturn(responseDefinitionBuilder);

        stubFor(responseForReturn);
    }

   /*настройка response, в случае неудачной обработки запроса*/
    void setupResponseInCaseFailedProcessingRequest(){

        UrlPattern urlPattern = urlEqualTo("/some/thing");

        MappingBuilder mappingBuilder = get(urlPattern);

        ResponseDefinitionBuilder serviceNotAvailable = aResponse().
                withStatus(503).
                withHeader("Content-Type", "text/html").
                withBody("Service Not Available");

        MappingBuilder contentForReturn = mappingBuilder
                .withHeader("Accept", matching("text/plain"))
                .willReturn(serviceNotAvailable);

        stubFor(contentForReturn);
    }


    @Test
    public void testServiceNotAvailable() {

        Header headerForRequest = new Header("Accept", "text/plain");

        RequestSpecification requestSpecification = given()
                .header(headerForRequest);

        Response response = requestSpecification
                .when()
                .get("/some/thing");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testForSuccessRequest() {

        Header headerForRequest = new Header("Accept", "application/json");

        Response response = given()
                .header(headerForRequest)
                .when()
                .get("/some/thing");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);

        String serviceStatus = response
                .jsonPath()
                .getString("serviceStatus");

        Assert.assertEquals(serviceStatus, "running");
    }

    @Test
    public void testRequestNotFound() {


        Header headerForRequest = new Header("Accept", "application/json");

        Response response = given()
                .header(headerForRequest)
                .when()
                .get("/some/thing/is/wrong");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_NOT_FOUND);
    }

}
