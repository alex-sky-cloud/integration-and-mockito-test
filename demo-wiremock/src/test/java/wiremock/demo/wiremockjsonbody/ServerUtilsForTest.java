package wiremock.demo.wiremockjsonbody;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import wiremock.demo.DemoWiremockApplicationTests;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerUtilsForTest extends DemoWiremockApplicationTests {

    @Value("${api.url.external.server}")
    private String apiUrlToExternalServer;

    @Value("${blockId.param.query}")
    private String blockId;

    @Value("${user.name.auth}")
    private String userNameAuth;

    @Value("${password.name.auth}")
    private String passwordNameAuth;

    public Integer getCountEntriesFromGottenResponse(ResponseEntity<ExternalServerDto> responseEntity) {

        List<Result> results = Objects.requireNonNull(responseEntity
                .getBody())
                .getRootElementOfODataProtocolDto()
                .getResults();
        return results.size();
    }



    /**
     * Возвращается список значений указанного заголовка (rec_count), иначе, выбрасывается Exception
     *
     * @param headers - карта заголовков из полученного ответа
     * @return список значений заголовка rec_count
     */
    public List<String> getListValueFromHeaderRecCount(HttpHeaders headers) {

        return headers.entrySet()
                .stream()
                .filter(keyHeader -> keyHeader.getKey().equals("rec_count"))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Не найден заголовок : " + "rec_count" + " в полученном ответе"));
    }

    public Integer getRecCountElementsExpected(List<String> listValueFromHeaderRecCount) {

        int indexOfListValueFromHeaderRecCount = 0;
        String recCountElementsFromResponseBody = listValueFromHeaderRecCount
                .get(indexOfListValueFromHeaderRecCount);

        return Integer.valueOf(recCountElementsFromResponseBody);
    }

    public ResponseEntity<ExternalServerDto> sendRequestToExternalServer(int portExternalServerMock) {

        String fullUrlToExternalServer = makeFullUrlToExternalServer(portExternalServerMock);
        UriComponentsBuilder uriComponentsBuilder = buildUriToExternalServer(fullUrlToExternalServer);
        String uriWithParamsToExternalServer = uriComponentsBuilder.toUriString();

        HttpHeaders requestHttpHeaders = getHeadersHttpHeaders();
        HttpEntity<Object> requestHttpEntity = new HttpEntity<>(null, requestHttpHeaders);

        return testRestTemplate.exchange(
                uriWithParamsToExternalServer,
                HttpMethod.GET,
                requestHttpEntity,
                ExternalServerDto.class
        );
    }

    private String makeFullUrlToExternalServer(int portExternalServerMock) {

        String prefixUrlToExternalServer = "http://127.0.0.1:";
        return prefixUrlToExternalServer +
                portExternalServerMock +
                this.apiUrlToExternalServer;

    }


    /**
     * Заполняет заголовок http request
     *
     * @return объект, описывающий заголовок http request
     */
    private HttpHeaders getHeadersHttpHeaders() {

        var requestHttpHeaders = new HttpHeaders();
        requestHttpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        addBasicAuth(requestHttpHeaders);

        return requestHttpHeaders;
    }

    /**
     * Создание адреса с параметрами для соединения со внешним сервером
     *
     * @param urlToExternalServer основная часть адреса
     * @return объект, содержащий необходимую информацию адреса для соединения с внешним сервером
     */
    private UriComponentsBuilder buildUriToExternalServer(String urlToExternalServer) {

        return UriComponentsBuilder.fromHttpUrl(urlToExternalServer)
                .queryParam("format", "json")
                .queryParam("block", blockId);
    }

    /**
     * настройка базовой аутентификации
     *
     * @param httpHeaders ссылка на объект, который содержит метаинформацию заголовка для
     *                    готовящегося запроса в смежную систему.
     */
    private void addBasicAuth(HttpHeaders httpHeaders) {

        String auth = this.userNameAuth + ":" + this.passwordNameAuth;

        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.US_ASCII));

        String authHeader = "Basic " + new String(encodedAuth);

        httpHeaders.add("Authorization", authHeader);

    }
}