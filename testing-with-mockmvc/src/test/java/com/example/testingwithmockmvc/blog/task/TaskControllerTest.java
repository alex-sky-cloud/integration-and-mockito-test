package com.example.testingwithmockmvc.blog.task;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void shouldRejectCreatingReviewsWhenUserIsAnonymous() throws Exception {

        this.mockMvc
                .perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"taskTitle\": \"Learn MockMvc\"}")
                                .with(csrf())
                )
                /*проверяем статус, сообщающий, что запрос от неавторизованного пользователя*/
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnLocationOfReviewWhenUserIsAuthenticatedAndCreatesReview() throws Exception {

        Long serviceTask = taskService.createTask(anyString());

        long idExpectedTask = 42L;

        when(serviceTask).thenReturn(idExpectedTask);

        ResultActions resultActions = this.mockMvc
                .perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"taskTitle\": \"Learn MockMvc\"}")
                                .with(csrf())
                                /**указываем пользователя, который будет аутентифицирован
                                 * и любые другие атрибуты, необходимые, чтобы попасть
                                 * на защищенный endpoint*/
                                .with(SecurityMockMvcRequestPostProcessors.user("duke"))
                );

        String redirectedUrl = resultActions.andReturn().getResponse().getRedirectedUrl();
        System.out.println(redirectedUrl);

        String idExpectedTaskInStr = String.valueOf(idExpectedTask);

        String uriRedirectExpected = "http://localhost/api/tasks/" + idExpectedTaskInStr;

        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(
                        header()
                                .string("Location", Matchers.containsString(idExpectedTaskInStr)
                                )
                )
                .andExpect(header()
                        .string("Location", Matchers.containsString(uriRedirectExpected)
                        )
                );
    }

    /**
     * указываем, что пользователь `duke` должен удалить запись,
     * при этом проверяем, что данному пользователю запрещено это сделать,
     * так как endpoint позволяет работать с ним пользователям,
     * с определенным уровнем доступа.
     * @throws Exception
     */
    @Test
    @WithMockUser("duke")
    void shouldRejectDeletingReviewsWhenUserLacksAdminRole() throws Exception {

        this.mockMvc
                .perform(delete("/api/tasks/42"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowDeletingReviewsWhenUserIsAdmin() throws Exception {

        this.mockMvc
                .perform(
                        delete("/api/tasks/42")
                                /**
                                 * указываем какие роли и какое имя пользователя
                                 * нужно использовать, чтобы выполнить защищенный endpoint
                                 */
                                .with(
                                        SecurityMockMvcRequestPostProcessors.user("duke")
                                                .roles("ADMIN", "SUPER_USER")
                                )
                                .with(csrf())
                )
                .andExpect(status().isOk());

        /**
         * используем макет (mock) сервиса, передаем туда параметр,
         * чтобы метод выполнился
         */
        verify(taskService).deleteTask(42L);
    }
}
