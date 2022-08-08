package com.example.testingwithmockmvc.blog.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
/**Это гарантирует, что spring полностью настроит
только компонент {@link UserController}, но не будет поднимать весь контекст **/
@WebMvcTest(UserController.class)
@WebAppConfiguration
class UserControllerAutowiredMockMvcTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockMvc mockMvc;

  /*для имитации зависимостей в контроллере UserController*/
  @MockBean
  private UserService userService;


  @Test
  @WithMockUser()/*для имитации http basic auth*/
  void shouldReturnAllUsersForUnauthenticatedUsers() throws Exception {

    /*mock for UserService*/
    when(userService.getAllUsers())
      .thenReturn(
              List.of(
                      new User("duke", "duke@spring.io")
              )
      );

    this.mockMvc
      .perform(MockMvcRequestBuilders.get("/api/users"))
      .andExpect(
              MockMvcResultMatchers.status().isOk()
      )
      .andExpect(
              MockMvcResultMatchers.jsonPath("$.size()").value(1)
      )
      .andExpect(
              MockMvcResultMatchers.jsonPath("$[0].username").value("duke")
      )
      .andExpect(
              MockMvcResultMatchers.jsonPath("$[0].email").value("duke@spring.io")
      );
  }

  @Test
  @WithMockUser()
  void shouldReturn404WhenUserIsNotFound() throws Exception {
    when(userService.getUserByUsername("duke"))
      .thenThrow(new UserNotFoundException("duke is not found"));

    this.mockMvc
      .perform(get("/api/users/duke"))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser()/*для имитации http basic auth*/
  void shouldAllowCreationForUnauthenticatedUsers() throws Exception {

    this.mockMvc
      .perform(
        post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"username\": \"duke\", \"email\":\"duke@spring.io\"}")
          .with(csrf())
      )
      .andExpect(status().isCreated())
            /**проверяем, что в header, мы получаем key, который в качестве
      значения будет содержать адрес перенаправления пользователя*/
      .andExpect(header().exists("Location"))
      .andExpect(header().string("Location", Matchers.containsString("duke")));

    verify(userService).storeNewUser(any(User.class));
  }
}
