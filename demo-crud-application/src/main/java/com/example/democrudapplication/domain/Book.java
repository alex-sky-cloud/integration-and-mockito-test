package com.example.democrudapplication.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class Book {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

}