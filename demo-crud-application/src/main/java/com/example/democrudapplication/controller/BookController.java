package com.example.democrudapplication.controller;

import com.example.democrudapplication.domain.Book;
import com.example.democrudapplication.domain.BookRequest;
import com.example.democrudapplication.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  /**
   * После создания объекта, пользователь будет перенаправлен на адрес,
   * который позволит запросить только что созданный объект
   * @param bookRequest - запрос пользователя на создание Book
   * @param uriComponentsBuilder - компонент, для постороения метаинформации, для перенаправления
   *                             пользователя
   * @return возвращает код успешного создания объекта и адрес перенарпавления на следующий
   * ресурс
   */
  @PostMapping
  public ResponseEntity<Void> createNewBook(@Valid @RequestBody BookRequest bookRequest,
                                            UriComponentsBuilder uriComponentsBuilder) {
    Long primaryKey = bookService.createNewBook(bookRequest);

    UriComponents uriComponents = uriComponentsBuilder
      .path("/api/books/{id}")
      .buildAndExpand(primaryKey);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());

    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAllBooks());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(bookService.getBookById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(@PathVariable("id") Long id,
                                         @Valid @RequestBody BookRequest bookRequest) {
    return ResponseEntity.ok(bookService.updateBook(id, bookRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
    bookService.deleteBookById(id);
    return ResponseEntity.ok().build();
  }
}
