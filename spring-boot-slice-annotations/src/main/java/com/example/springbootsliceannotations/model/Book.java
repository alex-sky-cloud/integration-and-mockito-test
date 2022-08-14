package com.example.springbootsliceannotations.model;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(force = true)
@Getter
@Setter
public class Book {

  @Id
  @GeneratedValue
  private Long id;

  @NaturalId
  private String isbn;

  @Column(nullable = false)
  private String title;
}
