package wiremock.demo.wiremockjsonbody;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalServerDto {

    String result;

    @JsonProperty("d")
    private RootElementOfODataProtocolDto rootElementOfODataProtocolDto;

    @JsonProperty("RootElementOfODataProtocolDto")
    public RootElementOfODataProtocolDto getRootElementOfODataProtocolDto() {
        return rootElementOfODataProtocolDto;
    }
}
