package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.enums.Gender;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.validator.DobConstrant;
import com.devteria.identity_service.validator.EnumPattern;
import com.devteria.identity_service.validator.GenderSubset;
import com.devteria.identity_service.validator.PhoneConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

import static com.devteria.identity_service.enums.Gender.FEMALE;
import static com.devteria.identity_service.enums.Gender.MALE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;
    @Size(min = 5, message = "INVALID_PASSWORD")
    String password;
    @Email(message = "INVALID_EMAIL")
    String email;
    String firstName;
    String lastName;
    @DobConstrant(min = 18, message = "INVALID_DOB")
    LocalDate dob;
    @GenderSubset(anyOf = {MALE, FEMALE})
    Gender gender;
    @EnumPattern(name = "userStatus", regexp = "ACTIVE|INACTIVE|NONE")
    UserStatus userStatus;
    //@Pattern(regexp = "/\\(?(\\d{3})\\)?([ .-]?)(\\d{3})\\2(\\dd{4})/")
    @PhoneConstraint(message = "INVALID_REGEX")
    private String phoneNumber;
    List<String> roles;
}
