package com.example.democrudapplication.controller;

import com.example.democrudapplication.domain.Book;
import com.example.democrudapplication.domain.BookRequest;
import com.example.democrudapplication.exceptions.BookNotFoundException;
import com.example.democrudapplication.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
/*указываем, что в контекст нужно инициализировать только компонент BookController,
* остальные зависимости будут заменены макетами*/
@WebMvcTest(BookController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerMockTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  /*создаем макет компонента BookService, так как проверяем только endpoint в Контроллере,
  * который нуждается в зависимости  BookService*/
  @MockBean
  private BookService bookService;

  /**
   * ArgumentCaptor позволяет нам перехватывать аргумент,
   * переданный методу, для его проверки. Это особенно полезно,
   * когда мы не можем получить доступ к аргументу за пределами метода,
   * который мы хотели бы протестировать.
   */
  @Captor
  private ArgumentCaptor<BookRequest> bookRequestArgumentCaptor;

  private BookRequest bookRequest;

  @BeforeAll
  void init() {
    /*когда данный объект будет создан, то ArgumentCaptor<BookRequest> захватит данный объект
     * и будет его заменять, то есть будет макетом*/
    bookRequest = new BookRequest();
    bookRequest.setAuthor("Duke");
    bookRequest.setIsbn("1337");
    bookRequest.setTitle("Java 11");
  }

  @Test
  void postingANewBookShouldCreateANewBook() throws Exception {

    /**
     * bookRequestArgumentCaptor.capture() захватит тип данных BookRequest,
     * когда данный объект поступит в качестве запроса на endpoint.
     * Затем, мы будем эти захваченные данные использовать, как макет, объекта
     * BookRequest, для проверки.
     */
    BookRequest captureBookRequest = bookRequestArgumentCaptor.capture();

    Long newBookExpected = bookService.createNewBook(captureBookRequest);

    Long idExpected = 1L;
    when(newBookExpected).thenReturn(idExpected);

    String jsonBookRequest = objectMapper.writeValueAsString(bookRequest);
    MockHttpServletRequestBuilder postRequestBuilder = post("/api/books");
    MockHttpServletRequestBuilder contentPostRequestBuilder = postRequestBuilder
      .contentType(MediaType.APPLICATION_JSON)
      .content(jsonBookRequest);/*формируем JSON-объект для передачи в endpoint*/

    /*отправляем запрос на endpoint*/
    this.mockMvc
      .perform(contentPostRequestBuilder)
      .andExpect(status().isCreated())
      .andExpect(header().exists("Location"))
      .andExpect(header().string("Location", "http://localhost/api/books/" + idExpected));

    String authorActual = bookRequestArgumentCaptor
      .getValue()
      .getAuthor();
    assertThat(authorActual, is("Duke"));

    String isbnActual  = bookRequestArgumentCaptor
      .getValue()
      .getIsbn();
    assertThat(isbnActual, is("1337"));

    String titleActual = bookRequestArgumentCaptor
      .getValue()
      .getTitle();
    assertThat(titleActual, is("Java 11"));
  }


  @Test
  void allBooksEndpointShouldReturnTwoBooks() throws Exception {

    when(bookService.getAllBooks()).thenReturn(List.of(
      createBook(1L, "Java 11", "Duke", "1337"),
      createBook(2L, "Java EE 8", "Duke", "1338")));

    this.mockMvc
      .perform(get("/api/books"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].title", is("Java 11")))
      .andExpect(jsonPath("$[0].author", is("Duke")))
      .andExpect(jsonPath("$[0].isbn", is("1337")))
      .andExpect(jsonPath("$[0].id", is(1)));

  }

  @Test
  void getBookWithIdOneShouldReturnABook() throws Exception {

    when(bookService.getBookById(1L)).thenReturn(createBook(1L, "Java 11", "Duke", "1337"));

    this.mockMvc
      .perform(get("/api/books/1"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$.title", is("Java 11")))
      .andExpect(jsonPath("$.author", is("Duke")))
      .andExpect(jsonPath("$.isbn", is("1337")))
      .andExpect(jsonPath("$.id", is(1)));

  }

  @Test
  void getBookWithUnknownIdShouldReturn404() throws Exception {

    when(bookService.getBookById(1L)).thenThrow(new BookNotFoundException("Book with id '1' not found"));

    this.mockMvc
      .perform(get("/api/books/1"))
      .andExpect(status().isNotFound());

  }

  @Test
  public void updateBookWithKnownIdShouldUpdateTheBook() throws Exception {

    BookRequest bookRequest = new BookRequest();
    bookRequest.setAuthor("Duke");
    bookRequest.setIsbn("1337");
    bookRequest.setTitle("Java 12");

    when(bookService.updateBook(eq(1L), bookRequestArgumentCaptor.capture()))
      .thenReturn(createBook(1L, "Java 12", "Duke", "1337"));

    this.mockMvc
      .perform(put("/api/books/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(bookRequest)))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$.title", is("Java 12")))
      .andExpect(jsonPath("$.author", is("Duke")))
      .andExpect(jsonPath("$.isbn", is("1337")))
      .andExpect(jsonPath("$.id", is(1)));

    assertThat(bookRequestArgumentCaptor.getValue().getAuthor(), is("Duke"));
    assertThat(bookRequestArgumentCaptor.getValue().getIsbn(), is("1337"));
    assertThat(bookRequestArgumentCaptor.getValue().getTitle(), is("Java 12"));

  }

  @Test
  public void updateBookWithUnknownIdShouldReturn404() throws Exception {

    BookRequest bookRequest = new BookRequest();
    bookRequest.setAuthor("Duke");
    bookRequest.setIsbn("1337");
    bookRequest.setTitle("Java 12");

    when(bookService.updateBook(eq(42L), bookRequestArgumentCaptor.capture()))
      .thenThrow(new BookNotFoundException("The book with id '42' was not found"));

    this.mockMvc
      .perform(put("/api/books/42")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(bookRequest)))
      .andExpect(status().isNotFound());

  }

  /**
   * вспомогательный метод
   */
  private Book createBook(Long id, String title, String author, String isbn) {

    Book book = new Book();
    book.setAuthor(author);
    book.setIsbn(isbn);
    book.setTitle(title);
    book.setId(id);
    return book;
  }

}
