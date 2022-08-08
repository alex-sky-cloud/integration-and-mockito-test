package com.example.testingwithmockmvc.blog.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GetMapping
    @RequestMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return this.userService.getUserByUsername(username);
    }

    /**
     * Когда новый пользователь будет создан, будет получено его имя,
     * и затем клиент будет перенаправлен на другой endpoint, который получит
     * данные о только что сохраненном пользователе, по его имени
     * @param user - данные по пользователю
     * @param uriComponentsBuilder - компонент, который настроит Spring и внедрит.
     *                             Этот компонент нужен для построения URI, для
     *                             перенаправления пользователя.
     * @return возвращает ссылку для перенаправления на endpoint, который
     * получает данные о пользователе, по его имени
     */
    @PostMapping
    public ResponseEntity<Void> createNewUser(
            @RequestBody @Valid User user,
            UriComponentsBuilder uriComponentsBuilder
    ) {

        this.userService.storeNewUser(user);
        String userName = user.getUsername();

        UriComponentsBuilder pathComponentsBuilder =
                uriComponentsBuilder.path("/api/users/{username}");
        URI uriRedirect = pathComponentsBuilder
                .build(userName);

        return ResponseEntity
                .created(uriRedirect)
                .build();
    }
}
