package com.example.springbootsliceannotations.controller;

import com.example.springbootsliceannotations.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RandomQuoteClient {

  private final RestTemplate restTemplate;

  public String getRandomQuote() {

    JsonNode jsonNode = this.restTemplate
            .getForObject("/qod", JsonNode.class);

    if(jsonNode == null){
      throw new NotFoundException("No found data there.");
    }

     return jsonNode
              .get("contents")
              .get("quotes").get(0)
              .get("quote").asText();

  }
}
