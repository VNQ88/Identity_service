package com.devteria.identity_service.controller_test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.response.UserRespone;
import com.devteria.identity_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest request;
    private UserRespone respone;

    private LocalDate dob;

    @BeforeEach
    public void initData() {
        dob = LocalDate.of(2000, 10, 20);
        request = UserCreationRequest.builder()
                .username("ronaldo")
                .firstName("cristiano")
                .lastName("ronaldo")
                .dob(dob)
                .email("ronaldo@gmail.com")
                .password("1234567")
                .build();

        respone = UserRespone.builder()
                .id("336bef95668a")
                .username("ronaldo")
                .firstName("cristiano")
                .lastName("ronaldo")
                .dob(dob)
                .email("ronaldo@gmail.com")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        when(userService.createUser(any())).thenReturn(respone);
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                // THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("336bef95668a"));
    }

    @Test
    void createUser_invalidUsername_fail() throws Exception {
        // GIVEN
        request.setUsername("hah");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                // THEN
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 characters"));
    }
}
