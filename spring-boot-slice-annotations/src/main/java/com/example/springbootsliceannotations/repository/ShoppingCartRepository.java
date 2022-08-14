package com.example.springbootsliceannotations.repository;

import com.example.springbootsliceannotations.model.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {
}
