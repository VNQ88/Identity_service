package com.devteria.identity_service.controller;

import java.util.List;

import com.devteria.identity_service.configuration.Translator;
import com.devteria.identity_service.dto.response.PageRespone;
import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.devteria.identity_service.dto.request.ApiRespone;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.dto.response.UserRespone;
import com.devteria.identity_service.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping()
    ApiRespone<UserRespone> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Controller: create user");
        log.info(Translator.toLocale("user.add.success"));
        ApiRespone<UserRespone> apiRespone = new ApiRespone<>();
        apiRespone.setMessage(Translator.toLocale("user.add.success"));
        apiRespone.setResult(userService.createUser(request));
        return apiRespone;
    }

    @GetMapping
    ApiRespone<List<UserRespone>> getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiRespone.<List<UserRespone>>builder()
                .result(userService.getUser())
                .build();
    }

    @GetMapping("/list")
    ApiRespone<PageRespone> getUserWithSortBy(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                              @RequestParam(defaultValue = "20", required = false) int pageSize,
                                              @RequestParam(required = false) String sortBy) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Username: {}", authentication.getName());
//        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiRespone.<PageRespone>builder()
                .code(1000)
                .result(userService.getAllUserWithSortBy(pageNo, pageSize, sortBy))
                .build();
    }

    @GetMapping("/list-sort-by-multiple-column")
    ApiRespone<PageRespone> getUserWithSortByMultipleColumn(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                              @RequestParam(defaultValue = "20", required = false) int pageSize,
                                              @RequestParam(required = false) String... sortBy) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Username: {}", authentication.getName());
//        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiRespone.<PageRespone>builder()
                .code(1000)
                .result(userService.getAllUserWithSortByMultipleColumn(pageNo, pageSize, sortBy))
                .build();
    }

    @GetMapping("/list-sort-by-multiple-column-search")
    ApiRespone<PageRespone> getAllUserWithSortByColumnAndSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                            @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                            @RequestParam(required = false) String sortBy,
                                                                @RequestParam(required = false) String search) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Username: {}", authentication.getName());
//        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiRespone.<PageRespone>builder()
                .code(1000)
                .result(userService.getAllUserWithSortByColumnAndSearch(pageNo, pageSize, sortBy, search))
                .build();
    }
    @GetMapping("/myInfo")
    ApiRespone<UserRespone> getMyInfo() {
        return ApiRespone.<UserRespone>builder().result(userService.getMyInfo()).build();
    }

    @GetMapping("/{userId}")
    ApiRespone<UserRespone> getUserById(@PathVariable("userId") String id) {
        return ApiRespone.<UserRespone>builder()
                .message(Translator.toLocale("user.getbyid.success"))
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/{userId}")
    ApiRespone<UserRespone> updateUser(@PathVariable("userId") String id, @RequestBody UserUpdateRequest request) {
        return ApiRespone.<UserRespone>builder()
                .message(Translator.toLocale("user.upd.success"))
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiRespone<Void> deleteUser(@PathVariable("userId") String id) {
        userService.deleteUser(id);
        return ApiRespone.<Void>builder()
                .message(Translator.toLocale("user.del.success"))
                .build();
    }

}
