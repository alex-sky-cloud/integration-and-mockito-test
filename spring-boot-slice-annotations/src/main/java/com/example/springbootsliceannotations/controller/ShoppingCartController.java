package com.example.springbootsliceannotations.controller;

import com.example.springbootsliceannotations.model.ShoppingCart;
import com.example.springbootsliceannotations.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class ShoppingCartController {

  private final ShoppingCartRepository shoppingCartRepository;

  @GetMapping
  public Iterable<ShoppingCart> getAllShoppingCarts() {
    return shoppingCartRepository.findAll();
  }
}
