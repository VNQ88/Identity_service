package com.devteria.identity_service.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.devteria.identity_service.enums.Gender;
import com.devteria.identity_service.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    String password;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String phoneNumber;
    Gender gender;
    UserStatus userStatus;
    List<String> roles;
}
