package com.example.springbootteststartexample.controller;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class PricingService {

  private final ProductVerifier productVerifier;
  private final ProductReporter productReporter;


  public BigDecimal calculatePrice(String productName) {

    boolean isCurrentlyInStockOfCompetitor =
            productVerifier.isCurrentlyInStockOfCompetitor(productName);

    /*если текущий продукт есть у конкурентов, мы печатаем отчет об этом
    * и назначаем новую цену для этого продукта*/
    if (isCurrentlyInStockOfCompetitor) {
      productReporter.notify(productName);
      return new BigDecimal("99.99");
    }

    /*если данного продукта нет у конкурентов, тогда мы
    * назначаем ему другую цену */
    return new BigDecimal("149.99");
  }
}