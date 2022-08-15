package com.example.springbootsliceannotations.controller;

import com.example.springbootsliceannotations.configuration.RestTemplateConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@RestClientTest(RandomQuoteClient.class)
@Import(RestTemplateConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RandomQuoteClientTest {

    @Autowired
    private RandomQuoteClient randomQuoteClient;

    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @BeforeAll
    void init(){
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldReturnQuoteFromRemoteSystem() {

        /*заготавливаем ответ, который должен будет вернуть сервер-макет*/
        String response = "{" +
                "\"contents\": {"+
                "\"quotes\": ["+
                "{"+
                "\"author\": \"duke\"," +
                "\"quote\": \"Lorem ipsum\""+
                "}"+
                "]"+
                "}" +
                "}";

        /**Когда запрос будет отправлен на endpoint, нужно будет проверить, что полный uri
        * будет соответствать базовой части, которая указана в конфигурационном классе
         * {@link RestTemplateConfiguration) и части, которая указана в endpoint
         * метод andRespond - вернет в endpoint выше заготовленный response, который будет
         * обработан в endpoint и возвращен сюда через вызов метода
         * {@link RandomQuoteClient#getRandomQuote()}
        * */
        this.mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://quotes.rest/qod"))
                .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.APPLICATION_JSON));

        String result = randomQuoteClient.getRandomQuote();

        assertEquals("Lorem ipsum", result);
    }

    @Test
   void shouldFailInCaseOfRemoteSystemBeingDown(){

        /*
        Здесь укажем, что сервер-макет должен сформировать ошибку (500 - внутренняя ошибка сервера)
        Поэтому, при попытке вызвать {@link RandomQuoteClient#getRandomQuote()}, будет выброшен
        RuntimeException, который мы и ожидаем в данном случае
         */
        this.mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://quotes.rest/qod"))
                .andRespond(MockRestResponseCreators.withServerError());

       assertThrows(RuntimeException.class, () -> randomQuoteClient.getRandomQuote());
    }
}