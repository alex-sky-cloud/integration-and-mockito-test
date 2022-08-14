package com.example.springbootsliceannotations.controller;

import com.example.springbootsliceannotations.model.Item;
import com.example.springbootsliceannotations.model.ShoppingCart;
import com.example.springbootsliceannotations.model.ShoppingCartItem;
import com.example.springbootsliceannotations.repository.ShoppingCartRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShoppingCartController.class)
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void shouldReturnAllShoppingCarts() throws Exception {

        /*товар для корзины*/
        Item macBook = new Item("MacBook", 999.9);

        /*имитируем добавление товара в корзину*/
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(macBook, 2);

        /*список товаров в корзине*/
        List<ShoppingCartItem> shoppingCartItemList = List.of(shoppingCartItem);

        /*создаем объект, который будет корзину с id и имитировать список товаров в корзине*/
        ShoppingCart shoppingCart = new ShoppingCart("42", shoppingCartItemList);

        /*создаем списко корзин*/
        List<ShoppingCart> shoppingCartList = List.of(shoppingCart);

        /*при обращении к макету (mock) репозитория (ShoppingCartRepository),
        * возвращаем ранее заготовленный список элементов */
        when(shoppingCartRepository.findAll())
                .thenReturn(shoppingCartList);

        this.mockMvc.perform(get("/api/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[0].id", Matchers.is("42")
                        )
                )
                .andExpect(jsonPath(
                                /*shoppingCartItems - это поле объекта ShoppingCart,
                                * список элементов которого, проверяем*/
                        "$[0].shoppingCartItems.length()", Matchers.is(1)
                        )
                )
                .andExpect(jsonPath(
                        "$[0].shoppingCartItems[0].item.name", Matchers.is("MacBook")
                        )
                )
                .andExpect(jsonPath(
                        "$[0].shoppingCartItems[0].quantity", Matchers.is(2)
                        )
                );
    }
}