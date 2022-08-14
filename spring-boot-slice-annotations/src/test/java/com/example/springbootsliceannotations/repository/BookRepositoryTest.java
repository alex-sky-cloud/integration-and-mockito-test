package com.example.springbootsliceannotations.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testCustomNativeQuery() {

        /*проверяем, что репозиторий инициализирован и запрос работает,
        * даже если элементов нет*/
        assertEquals(0, bookRepository.findAll().size());

        /*проверяем, что прошла инициализация DataSource и EntityManager*/
        assertNotNull(dataSource);
        assertNotNull(entityManager);
    }
}