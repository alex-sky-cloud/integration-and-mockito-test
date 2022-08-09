package com.example.testingwithmockmvc.blog.dashboard;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
class DashboardControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DashboardService dashboardService;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
  }

  @Test
  @WithMockUser
  void shouldReturnViewWithPrefilledData() throws Exception {

    /*макет сервисного метода*/
    Integer[] analyticsGraphData =
            dashboardService.getAnalyticsGraphData();

    /*создаем фиктивные данные для макетного сервиса*/
    when(analyticsGraphData)
            .thenReturn(new Integer[]{13, 42});

    MockHttpServletRequestBuilder httpServletRequestBuilder = get("/dashboard");
    this.mockMvc
      .perform(httpServletRequestBuilder)
      .andExpect(status().isOk())
      .andExpect(view().name("dashboard"))/*имя шаблона*/
       /*проверяем что шаблон содержит указанные данные*/
      .andExpect(model().attribute("user", "Duke"))
      .andExpect(model().attribute("analyticsGraph", Matchers.arrayContaining(13, 42)))
      /*проверяем, что указанный атрибут уже существует*/
      .andExpect(model().attributeExists("quickNote"));
  }
}
