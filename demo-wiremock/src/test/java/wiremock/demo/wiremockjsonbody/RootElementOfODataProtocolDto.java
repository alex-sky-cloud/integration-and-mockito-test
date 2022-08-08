package wiremock.demo.wiremockjsonbody;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RootElementOfODataProtocolDto {

    @JsonProperty("results")
    private List<Result> results = new ArrayList<Result>();

    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }
}
