package com.example.springbootsliceannotations;

import com.example.springbootsliceannotations.controller.RandomQuoteClient;
import com.example.springbootsliceannotations.repository.BookRepository;
import com.example.springbootsliceannotations.repository.ShoppingCartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static com.mongodb.assertions.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootSliceAnnotationsApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RandomQuoteClient randomQuoteClient;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void contextLoads() {
        assertNotNull(testRestTemplate);
        assertNotNull(randomQuoteClient);
        assertNotNull(shoppingCartRepository);
        assertNotNull(bookRepository);
    }
}
