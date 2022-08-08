package wiremock.demo.wiremockjsonbody;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ServerMockClientConfig {

    @Autowired
    private ServerMockUtils serverMockUtils;

    @Value("${api.url.fail.header.rec.count.for.stubs}")
    private String apiUrlFailHeaderRecCountToExternalServer;

    @Value("${api.url.fail.status.response.for.stubs}")
    private String apiUrlFailResponseExternalServer;

    public void setupStubForProcessingRequest(int portExternalServerMock) {

        String addressHost = "127.0.0.1";

        configureFor(addressHost, portExternalServerMock);

        setupResponseInCaseFailRecCount();
        setupResponseWithFailHttpStatus();

    }

    /**
     * заглушка эмулирует работу внешней системы, когда
     * она отправляет статус отличный от статуса : 200
     */
    private void setupResponseWithFailHttpStatus(){

        UrlPathPattern urlPathPattern = urlPathEqualTo(this.apiUrlFailResponseExternalServer);

        MappingBuilder mappingBuilder = get(urlPathPattern);

        MappingBuilder mappingBuilderWithHeader = serverMockUtils.makeMappingBuilderSuccess(mappingBuilder);

        int statusServiceUnavailable = HttpStatus.SERVICE_UNAVAILABLE.value();

        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse().
                withStatus(statusServiceUnavailable)
                .withHeader("Content-Type", "application/json")
                .withBody("Service Not Available");

        MappingBuilder responseForReturn = mappingBuilderWithHeader.willReturn(responseDefinitionBuilder);

        stubFor(responseForReturn);
    }

    /**
     * Заглушка будем эмулировать ошибочный возврат количества записей
     *  в значении Header of Request : "rec_count", чтобы проверить валидацию данного
     *  заголовка в rest-client приложения
     *  Запрос будет отправлен из тестового метода, на указанный адрес, в методе urlPathEqualTo()
     */
    private void setupResponseInCaseFailRecCount(){

        String countEntriesIntoHeaderRecCount = "1";

        UrlPathPattern urlPathPattern = urlPathEqualTo(this.apiUrlFailHeaderRecCountToExternalServer);

        MappingBuilder mappingBuilder = get(urlPathPattern);

        MappingBuilder mappingBuilderWithHeader = serverMockUtils.makeMappingBuilderSuccess(mappingBuilder);

        int statusOk = HttpStatus.OK.value();

        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse().
                withStatus(statusOk)
                .withHeader("Content-Type", "application/json")
                .withHeader("rec_count", countEntriesIntoHeaderRecCount)
                .withBodyFile("json/InfoFromExternalServer.json");

        MappingBuilder responseForReturn = mappingBuilderWithHeader.willReturn(responseDefinitionBuilder);

        stubFor(responseForReturn);
    }

}
