package com.devteria.identity_service.controller_test;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.response.UserRespone;
import com.devteria.identity_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {
    @Container
    static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }
    @Autowired
    private MockMvc mockMvc;

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

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                // THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value(respone.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName").value(respone.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName").value(respone.getLastName()));

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
