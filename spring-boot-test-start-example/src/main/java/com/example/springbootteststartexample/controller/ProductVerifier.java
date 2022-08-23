package com.example.springbootteststartexample.controller;

public class ProductVerifier {

  /**
   * проверяем, является ли продукт в текущем хранилище, в наличии у
   * конкурентов
   * @param productName имя продукта
   */
  public boolean isCurrentlyInStockOfCompetitor(String productName) {
    return false;
  }
}