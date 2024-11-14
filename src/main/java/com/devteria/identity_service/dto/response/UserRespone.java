package com.devteria.identity_service.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.devteria.identity_service.enums.Gender;
import com.devteria.identity_service.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRespone {
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String gender;
    String userStatus;
    Set<RoleRespone> roles;
}
