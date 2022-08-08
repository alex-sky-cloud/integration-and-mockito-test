package wiremock.demo.wiremockjsonbody;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockForRestControllerConfig {
    @Autowired
    private ServerMockUtils serverMockUtils;

    @Value("${api.url.external.server}")
    private String apiUrlToExternalServer;

    public void setupStubForProcessingRequest(int portExternalServerMock) {

        String addressHost = "127.0.0.1";

        configureFor(addressHost, portExternalServerMock);

        setupResponseInCaseSuccessProcessingRequest();
    }

    /**
     * настройка response в случае успешной обработки запроса
     * <p><d>countEntriesIntoHeaderRecCount</d> - эта величина должна соответствовать
     * количеству массивов в поле 'results' из файла *.json, который вы предоставляете в качестве
     * тестового файла для заглушки WireMock-сервера
     * </p>*/
    private void setupResponseInCaseSuccessProcessingRequest(){

        String countEntriesIntoHeaderRecCount = "2";

        UrlPathPattern urlPathPattern = urlPathEqualTo(this.apiUrlToExternalServer);

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
