package com.example.democrudapplication.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/*Инструктирует JUnit Jupiter, что нужно расширить тест, с помощью
Spring TestContext. Это значит, что будет поднят весь контекст приложения.
В данном случае, нужно чтобы был поднят весь контекст, так как в момент его
инициализации, будет создана in-memory H2 и инициализирована данными, как мы
это определили в одном из классов*/
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTestIT {
  @LocalServerPort
  int randomServerPort;

  private TestRestTemplate testRestTemplate;

  @BeforeEach
  public void setUp() {
    this.testRestTemplate = new TestRestTemplate();
  }

  @Test
  void deletingKnownEntityShouldReturn404AfterDeletion() {

    long bookId = 1;
    String baseUrl = "http://localhost:" + randomServerPort;

    ResponseEntity<JsonNode> firstResult = this.testRestTemplate
      .getForEntity(baseUrl + "/api/books/" + bookId, JsonNode.class);

    assertThat(firstResult.getStatusCode(), is(HttpStatus.OK));

    this.testRestTemplate.delete(baseUrl + "/api/books/" + bookId);

    ResponseEntity<JsonNode> secondResult = this.testRestTemplate
      .getForEntity(baseUrl + "/api/books/" + bookId, JsonNode.class);

    assertThat(secondResult.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }
}