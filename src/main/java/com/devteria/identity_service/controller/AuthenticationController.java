package com.devteria.identity_service.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devteria.identity_service.dto.request.*;
import com.devteria.identity_service.dto.response.AuthenticationRespone;
import com.devteria.identity_service.dto.response.IntrospectRespone;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiRespone<AuthenticationRespone> genToken(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiRespone.<AuthenticationRespone>builder()
                .code(1000)
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiRespone<AuthenticationRespone> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refresh(request);
        return ApiRespone.<AuthenticationRespone>builder()
                .code(1000)
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiRespone<IntrospectRespone> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiRespone.<IntrospectRespone>builder().code(1000).result(result).build();
    }

    @PostMapping("/logout")
    ApiRespone<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiRespone.<Void>builder().code(1000).build();
    }
}
