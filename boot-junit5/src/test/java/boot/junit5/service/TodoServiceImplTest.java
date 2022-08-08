package boot.junit5.service;


import boot.junit5.model.Todo;
import boot.junit5.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TodoServiceImplTest {

    private TodoServiceImpl uut;
    @Mock
    private TodoRepository repositoryMock;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        uut = new TodoServiceImpl(repositoryMock);
    }

    @Nested
    class Save {
        @Test
        public void should_Save() {
            Todo todo = newTodo();
            Todo persisted = newTodo();
            when(repositoryMock.save(todo)).thenReturn(persisted);
            Todo saved = uut.save(todo);
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(persisted.getId());
            verify(repositoryMock, times(1)).save(todo);
        }
    }

    @Test
    public void findAll() {
        // should be implemented
    }

    @Test
    public void deleteById() {
        // should be implemented
    }

    private Todo newTodo() {
        return Todo.newBuilder()
                .title("fix bug")
                .note("a bug was found and we have to fix it")
                .owner("Homer Simpson")
                .build();
    }
}
