package com.example.democrudapplication.service;

import com.example.democrudapplication.domain.Book;
import com.example.democrudapplication.domain.BookRequest;
import com.example.democrudapplication.exceptions.BookNotFoundException;
import com.example.democrudapplication.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  @Transactional
  public Long createNewBook(BookRequest bookRequest) {

    Book book = new Book();
    book.setIsbn(bookRequest.getIsbn());
    book.setAuthor(bookRequest.getAuthor());
    book.setTitle(bookRequest.getTitle());

    book = bookRepository.save(book);

    return book.getId();
  }

  @Transactional(readOnly = true)
  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Book getBookById(Long id) {
    Optional<Book> requestedBook = bookRepository.findById(id);

    /**В случае, если данный блок выполниться, то будет сформирован автоматически
    * ответ для клиента, так как над {@link BookNotFoundException} установлена соответствующая
     * аннотация*/
    if (requestedBook.isEmpty()) {
      throw new BookNotFoundException(String.format("Book with id: '%s' not found", id));
    }

    return requestedBook.get();
  }

  @Transactional
  public Book updateBook(Long id, BookRequest bookToUpdateRequest) {

    Optional<Book> bookFromDatabase = bookRepository.findById(id);

    /**В случае, если данный блок выполниться, то будет сформирован автоматически
     * ответ для клиента, так как над {@link BookNotFoundException} установлена соответствующая
     * аннотация*/
    if (bookFromDatabase.isEmpty()) {
      throw new BookNotFoundException(String.format("Book with id: '%s' not found", id));
    }

    Book bookToUpdate = bookFromDatabase.get();

    bookToUpdate.setAuthor(bookToUpdateRequest.getAuthor());
    bookToUpdate.setIsbn(bookToUpdateRequest.getIsbn());
    bookToUpdate.setTitle(bookToUpdateRequest.getTitle());

    return bookToUpdate;
  }

  @Transactional
  public void deleteBookById(Long id) {
    bookRepository.deleteById(id);
  }
}
