package boot.junit5.service;


import boot.junit5.model.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoService {

    Todo save(Todo todo);

    Optional<Todo> findById(Long id);

    List<Todo> findAll();

    void deleteById(Long id);
}
