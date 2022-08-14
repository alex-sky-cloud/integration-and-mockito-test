package com.example.springbootsliceannotations.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShoppingCart {

    @Id
    private String id;

    private List<ShoppingCartItem> shoppingCartItems;
}
