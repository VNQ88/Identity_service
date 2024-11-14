package com.devteria.identity_service.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    String name;

    String description;

    @ManyToMany(fetch = FetchType.EAGER)
    Set<Permission> permissions;
}
