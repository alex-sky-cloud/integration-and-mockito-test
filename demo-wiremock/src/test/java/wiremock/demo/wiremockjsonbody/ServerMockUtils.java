package wiremock.demo.wiremockjsonbody;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;

@Component
public class ServerMockUtils  {


    @Value("${blockId.param.query}")
    private String blockId;

    @Value("${user.name.auth}")
    private String userNameAuth;

    @Value("${password.name.auth}")
    private String passwordNameAuth;

    public MappingBuilder makeMappingBuilderSuccess(MappingBuilder mappingBuilder){

        return mappingBuilder
                .withHeader("Accept", matching("application/json"))
                .withQueryParam("format", equalTo("json"))
                .withQueryParam("block", equalTo(blockId))
                .withBasicAuth(this.userNameAuth, this.passwordNameAuth);
    }
}
