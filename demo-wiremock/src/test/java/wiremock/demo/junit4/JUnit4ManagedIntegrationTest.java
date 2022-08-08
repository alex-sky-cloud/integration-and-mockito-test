package wiremock.demo.junit4;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.Assert.assertEquals;

/**
 * JUnit4
 */
public class JUnit4ManagedIntegrationTest {

    private static final String BAELDUNG_WIREMOCK_PATH = "/baeldung/wiremock";
    private static final String APPLICATION_JSON = "application/json";
    static int port;

    /**
     * получаем случайный порт из доступных свободных портов в системе
     */
    static {

        try {
            // Получаем случайный порт на текущем хосте
            ServerSocket serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);


    @Test
    public void givenJUnitManagedServer_whenMatchingURL_thenCorrect() throws IOException {

        String patternUrl = "/baeldung/.*";
        UrlPathPattern urlPathPattern = urlPathMatching(patternUrl);
        MappingBuilder mappingBuilder = get(urlPathPattern);

        String jsonData = "\"testing-library\": \"WireMock\"";
        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse()
                .withStatus(200)
                .withHeader("Content-Type", APPLICATION_JSON)
                .withBody(jsonData);

        MappingBuilder willReturn = mappingBuilder
                .willReturn(responseDefinitionBuilder);

        stubFor(willReturn);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String formatUrl = String.format("http://localhost:%s/baeldung/wiremock", port);
        HttpGet request = new HttpGet(formatUrl);
        HttpResponse httpResponse = httpClient.execute(request);

        String stringResponse = convertHttpResponseToString(httpResponse);

        UrlPattern urlPattern = urlEqualTo(BAELDUNG_WIREMOCK_PATH);
        RequestPatternBuilder requestedFor = getRequestedFor(urlPattern);
        verify(requestedFor);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());

        String valueFromHeader = httpResponse.getFirstHeader("Content-Type").getValue();
        assertEquals(APPLICATION_JSON, valueFromHeader );

        assertEquals("\"testing-library\": \"WireMock\"", stringResponse);
    }


    @Test
    public void givenJUnitManagedServer_whenMatchingHeaders_thenCorrect() throws IOException {

        UrlPathPattern urlPathPattern = urlPathEqualTo(BAELDUNG_WIREMOCK_PATH);
        MappingBuilder mappingBuilder = get(urlPathPattern);
        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse()
                .withStatus(503)
                .withHeader("Content-Type", "text/html")
                .withBody("!!! Service Unavailable !!!");

        MappingBuilder accept = mappingBuilder
                .withHeader("Accept", matching("text/.*"))
                .willReturn(responseDefinitionBuilder);

        stubFor(accept);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(String.format("http://localhost:%s/baeldung/wiremock", port));

        request.addHeader("Accept", "text/html");

        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = convertHttpResponseToString(httpResponse);

        verify(getRequestedFor(urlEqualTo(BAELDUNG_WIREMOCK_PATH)));

        assertEquals(503, httpResponse.getStatusLine().getStatusCode());

        assertEquals("text/html", httpResponse.getFirstHeader("Content-Type").getValue());

        assertEquals("!!! Service Unavailable !!!", stringResponse);

    }


    @Test
    public void givenJUnitManagedServer_whenMatchingBody_thenCorrect() throws IOException {

        stubFor(post(urlEqualTo(BAELDUNG_WIREMOCK_PATH))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON))

                .withRequestBody(containing("\"testing-library\": \"WireMock\""))
                .withRequestBody(containing("\"creator\": \"Tom Akehurst\""))
                .withRequestBody(containing("\"website\": \"wiremock.org\""))

                .willReturn(aResponse().withStatus(200)));


        //объектное представление тела запроса
        InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
        String jsonString = convertInputStreamToString(jsonInputStream);
        StringEntity entity = new StringEntity(jsonString);

        //client
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(String.format("http://localhost:%s/baeldung/wiremock", port));
        request.addHeader("Content-Type", APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        verify(postRequestedFor(urlEqualTo(BAELDUNG_WIREMOCK_PATH))
                .withHeader("Content-Type", equalTo(APPLICATION_JSON)));
        assertEquals(200, response.getStatusLine().getStatusCode());

    }

    @Test
    public void givenJUnitManagedServer_whenNotUsingPriority_thenCorrect() throws IOException {
       //первый url заглушка на сервере
        stubFor(
                get(urlPathMatching("/baeldung/.*"))
                        .willReturn(aResponse()
                                .withStatus(200)
                        ));
        //второй url заглушка на сервере
        // "/baeldung/wiremock";
        stubFor(
                get(urlPathEqualTo(BAELDUNG_WIREMOCK_PATH))
                        .withHeader("Accept", matching("text/.*"))
                        .willReturn(aResponse()
                                .withStatus(503)
                        ));

        HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();

        verify(getRequestedFor(urlEqualTo(BAELDUNG_WIREMOCK_PATH)));
        assertEquals(503, httpResponse.getStatusLine().getStatusCode());


    }

    @Test
    public void givenJUnitManagedServer_whenUsingPriority_thenCorrect() throws IOException {

        stubFor(
                get(urlPathMatching("/baeldung/.*"))
                        .atPriority(1)
                        .willReturn(aResponse()
                                .withStatus(200)
                        ));
        stubFor(
                get(
                        urlPathEqualTo(BAELDUNG_WIREMOCK_PATH))
                        .atPriority(2)
                        .withHeader("Accept", matching("text/.*"))
                        .willReturn(aResponse()
                                .withStatus(503)
                        ));

        HttpResponse httpResponse = generateClientAndReceiveResponseForPriorityTests();

        verify(getRequestedFor(urlEqualTo(BAELDUNG_WIREMOCK_PATH)));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    private HttpResponse generateClientAndReceiveResponseForPriorityTests() throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(String.format("http://localhost:%s/baeldung/wiremock", port));

        request.addHeader("Accept", "text/xml");

        return httpClient.execute(request);
    }


    private static String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertInputStreamToString(inputStream);
    }

    private static String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }

}