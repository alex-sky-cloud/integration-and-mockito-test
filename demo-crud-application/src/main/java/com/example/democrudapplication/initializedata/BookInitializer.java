package com.example.democrudapplication.initializedata;

import com.example.democrudapplication.domain.Book;
import com.example.democrudapplication.repository.BookRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookInitializer implements CommandLineRunner {

  private final BookRepository bookRepository;

  /**
   *во время загрузки приложения, будет создан макет доменной сущности,
   * который будет заполнен данными и за одну итерацию, он будет сохранен в базе данных
   * H2, которая будет развернута in-memory.
   * Будет выполнено несколько итераций в цикле
   */
  @Override
  public void run(String... args) {

    Faker faker = new Faker();

    log.info("Starting book initialization ...");

    for (int i = 0; i < 10; i++) {

      Book book = new Book();
      book.setTitle(faker.book().title());
      book.setAuthor(faker.book().author());
      book.setIsbn(UUID.randomUUID().toString());

      bookRepository.save(book);
    }

    log.info("... finished book initialization");

  }
}
