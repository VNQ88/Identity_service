package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.Gender;
import com.devteria.identity_service.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String phoneNumber;
    @Enumerated(EnumType.STRING)
    Gender gender;
    @Enumerated(EnumType.STRING)
    UserStatus userStatus;
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles;

}
