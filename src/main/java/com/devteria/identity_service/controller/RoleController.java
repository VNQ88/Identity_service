package com.devteria.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.devteria.identity_service.dto.request.ApiRespone;
import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.response.RoleRespone;
import com.devteria.identity_service.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiRespone<RoleRespone> create(@RequestBody RoleRequest request) {
        return ApiRespone.<RoleRespone>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiRespone<List<RoleRespone>> getAll() {
        return ApiRespone.<List<RoleRespone>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiRespone<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return ApiRespone.<Void>builder().build();
    }
}
