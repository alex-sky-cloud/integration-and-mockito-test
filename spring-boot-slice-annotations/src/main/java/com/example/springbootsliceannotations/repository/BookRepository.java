package com.example.springbootsliceannotations.repository;

import com.example.springbootsliceannotations.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
