package com.example.springbootteststartexample.controller;

public class ProductReporter {

  /*отчет о запрашиваемом продукте*/
  public void notify(String productName) {
    System.out.println(productName + " is currently in stock of the competitor");
  }
}
