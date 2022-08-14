package com.example.springbootsliceannotations.repository;

import com.example.springbootsliceannotations.model.Item;
import com.example.springbootsliceannotations.model.ShoppingCart;
import com.example.springbootsliceannotations.model.ShoppingCartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ShoppingCartRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    public void shouldCreateContext() {

        /*элемент корзины, вещь которую покупают*/
        Item macBook = new Item("MacBook", 999.9);

        /*в корзине появилась позиция элемента, в количестве 2 штук(клиент покупает 2 MacBook)*/
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(macBook, 2);

        /*таких элементов список, то есть корзина клиента со списком покупок (пока только 1 вещь)*/
        List<ShoppingCartItem> shoppingCartItemList = List.of(shoppingCartItem);

        /*Это контейнер для корзины клиента. Данному контейнеру присвоен id = 42*/
        ShoppingCart shoppingCart = new ShoppingCart("42", shoppingCartItemList);

        shoppingCartRepository.save(shoppingCart);

        assertNotNull(mongoTemplate);
        assertNotNull(shoppingCartRepository);
    }
}