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

    String isbnActual = bookRequestArgumentCaptor
      .getValue()
      .getIsbn();
    assertThat(isbnActual, is("1337"));

    String titleActual = bookRequestArgumentCaptor
      .getValue()
      .getTitle();
    assertThat(titleActual, is("Java 11"));
  }


  /*проверяем запрос на endpoint*/
  @Test
  void allBooksEndpointShouldReturnTwoBooks() throws Exception {

    List<Book> bookListExpected = List.of(
      createBook(1L, "Java 11", "Duke", "1337"),
      createBook(2L, "Java EE 8", "Duke", "1338")
    ); /*формируем список элементов, который возвратит макет*/

    List<Book> allBooks = bookService.getAllBooks();

    /*указываем ответ, который должен вернуть макет сервиса BookService*/
    when(allBooks)
      .thenReturn(bookListExpected);

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

  /*Проверка запроса с получением одной единицы объекта*/
  @Test
  void getBookWithIdOneShouldReturnABook() throws Exception {

    Book book = createBook(1L, "Java 11", "Duke", "1337");

    /*указываем данные, которые должен вернуть макет*/
    when(bookService.getBookById(1L)).thenReturn(book);

    this.mockMvc
      .perform(get("/api/books/1"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$.title", is("Java 11")))
      .andExpect(jsonPath("$.author", is("Duke")))
      .andExpect(jsonPath("$.isbn", is("1337")))
      .andExpect(jsonPath("$.id", is(1)));

  }

  /*Проверяем, что в случае запроса, если запрашиваемого ресурса нет,
   * будет сформирован ответ об ошибке*/
  @Test
  void getBookWithUnknownIdShouldReturn404() throws Exception {

    BookNotFoundException bookNotFoundException = new BookNotFoundException("Book with id '1' not found");
    Book bookById = bookService.getBookById(1L);

    /*Указываем, что когда будет вызван макет сервиса BookService, тогда будет имитирован выброс Exception,
     * который в свою очередь будет перехвачен обработчиком Exceptions и затем будет сформирован,
     * автоматически ответ с ошибкой (для клиента)*/
    when(bookById)
      .thenThrow(bookNotFoundException);

    /*ожидаем ответ, который сообщит о том, что данного ресурса нет*/
    this.mockMvc
      .perform(get("/api/books/1"))
      .andExpect(status().isNotFound());

  }


  @Test
  void updateBookWithKnownIdShouldUpdateTheBook() throws Exception {

    /*Данные для обновления, будут получены в тот момент, когда
     * на endpoint '/api/books/1' поступят параметры для обновления.
     * Затем данные из этого же объекта, будут использованы для проверки, того,
     * что эти фактические данные, совпадают с ожидаемыми*/

    Long idBookActual = eq(1L);
    this.bookRequest.setTitle("Java 12");

    BookRequest bookRequestArgumentCaptureActual = bookRequestArgumentCaptor.capture();
    Book updateBook = bookService.updateBook(idBookActual, bookRequestArgumentCaptureActual);

    Long idBookExpected = 1L;
    String titleExpected = "Java 12";
    String authorExpected = "Duke";
    String isbnExpected = "1337";
    Book bookMockExpected = createBook(idBookExpected, titleExpected, authorExpected, isbnExpected);

    when(updateBook)
      .thenReturn(bookMockExpected);


    String updateBookInJson = objectMapper.writeValueAsString(this.bookRequest);

    this.mockMvc
      .perform(put("/api/books/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateBookInJson)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json"))
      .andExpect(jsonPath("$.title", is(titleExpected)))
      .andExpect(jsonPath("$.author", is(authorExpected)))
      .andExpect(jsonPath("$.isbn", is(isbnExpected)))
      .andExpect(jsonPath("$.id", is(1)));  /*важно. Здесь нельзя использовать суффикс L или F*/


    assertThat(bookRequestArgumentCaptor.getValue().getAuthor(), is(authorExpected));
    assertThat(bookRequestArgumentCaptor.getValue().getIsbn(), is(isbnExpected));
    assertThat(bookRequestArgumentCaptor.getValue().getTitle(), is(titleExpected));
  }

  @Test
  void updateBookWithUnknownIdShouldReturn404() throws Exception {

    Long idBookRequested = eq(42L);
    BookRequest bookRequestActual = bookRequestArgumentCaptor.capture();

    Book updateBook = bookService.updateBook(idBookRequested, bookRequestActual);
    when(updateBook)
      .thenThrow(new BookNotFoundException("The book with id '42' was not found"));

    this.mockMvc
      .perform(put("/api/books/42")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
          objectMapper.writeValueAsString(this.bookRequest))
      )
      .andExpect(
        status().isNotFound()
      );
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
