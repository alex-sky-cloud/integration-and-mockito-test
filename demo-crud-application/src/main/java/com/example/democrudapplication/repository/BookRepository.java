package com.example.democrudapplication.repository;

import com.example.democrudapplication.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
