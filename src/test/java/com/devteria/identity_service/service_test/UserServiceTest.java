package com.devteria.identity_service.service_test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.response.UserRespone;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.repository.UserRepository;
import com.devteria.identity_service.service.UserService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@TestPropertySource("/test.properties")
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserRespone userRespone;
    private User user;
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

        userRespone = UserRespone.builder()
                .id("336bef95668a")
                .username("ronaldo")
                .firstName("cristiano")
                .lastName("ronaldo")
                .dob(dob)
                .email("ronaldo@gmail.com")
                .build();

        user = User.builder()
                .id("336bef95668a")
                .username("ronaldo")
                .firstName("cristiano")
                .lastName("ronaldo")
                .dob(dob)
                .email("ronaldo@gmail.com")
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        // WHEN
        var respone = userService.createUser(request);
        // THEN
        Assertions.assertThat(respone.getId()).isEqualTo(userRespone.getId());
        Assertions.assertThat(respone.getUsername()).isEqualTo(userRespone.getUsername());
    }

    @Test
    void createUser_userExisted_fail() {
        // GIVEN
        // WHEN
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    }

    @Test
    @WithMockUser(username = "messi", roles = "ADMIN")
    void getMyInfo_validRequest_success() {
        // GIVEN

        // WHEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        var respone = userService.getMyInfo();
        // THEN
        Assertions.assertThat(respone.getId()).isEqualTo(userRespone.getId());
        Assertions.assertThat(respone.getUsername()).isEqualTo(userRespone.getUsername());
    }

    @Test
    @WithMockUser(username = "messi")
    void getMyInfo_userNotExisted_fail() {
        // GIVEN
        // WHEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1006);
    }
}
