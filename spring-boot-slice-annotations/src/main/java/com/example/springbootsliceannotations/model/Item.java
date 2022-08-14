package com.example.springbootsliceannotations.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Item {

  @Id
  private String id;
  private String name;
  private double price;

  private Item() {
  }

  public Item(String name, double price) {
    this.name = name;
    this.price = price;
  }
}
