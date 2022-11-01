package com.github.prgrms.social.api.controller.user;

import com.github.prgrms.social.api.security.WithMockJwtAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRestControllerTest {

  private MockMvc mockMvc;

  @Autowired
  public void setMockMvc(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  @WithMockJwtAuthentication
  @DisplayName("내 정보 조회 성공 테스트 (토큰이 올바른 경우)")
  void meSuccessTest() throws Exception {
    ResultActions result = mockMvc.perform(
      get("/api/user/me")
        .accept(MediaType.APPLICATION_JSON)
    );
    result.andDo(print())
      .andExpect(status().isOk())
      .andExpect(handler().handlerType(UserRestController.class))
      .andExpect(handler().methodName("me"))
      .andExpect(jsonPath("$.success", is(true)))
      .andExpect(jsonPath("$.response.name", is("tester00")))
      .andExpect(jsonPath("$.response.email.address", is("test00@gmail.com")))
      .andExpect(jsonPath("$.response.loginCount").exists())
      .andExpect(jsonPath("$.response.loginCount").isNumber())
      .andExpect(jsonPath("$.response.createAt").exists())
    ;
  }

}