package com.devteria.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.devteria.identity_service.dto.request.ApiRespone;
import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.response.PermissionRespone;
import com.devteria.identity_service.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiRespone<PermissionRespone> create(@RequestBody PermissionRequest request) {
        return ApiRespone.<PermissionRespone>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiRespone<List<PermissionRespone>> getAll() {
        return ApiRespone.<List<PermissionRespone>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiRespone<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return ApiRespone.<Void>builder().build();
    }
}
