package com.devteria.identity_service.service;

import java.text.ParseException;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.IntrospectRequest;
import com.devteria.identity_service.dto.request.LogoutRequest;
import com.devteria.identity_service.dto.request.RefreshRequest;
import com.devteria.identity_service.dto.response.AuthenticationRespone;
import com.devteria.identity_service.dto.response.IntrospectRespone;
import com.nimbusds.jose.JOSEException;

public interface AuthenticationService {
    AuthenticationRespone authenticate(AuthenticationRequest authenticationRequest);

    IntrospectRespone introspect(IntrospectRequest request) throws JOSEException, ParseException;

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    AuthenticationRespone refresh(RefreshRequest request) throws ParseException, JOSEException;
}
