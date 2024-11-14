package com.devteria.identity_service.service.impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.IntrospectRequest;
import com.devteria.identity_service.dto.request.LogoutRequest;
import com.devteria.identity_service.dto.request.RefreshRequest;
import com.devteria.identity_service.dto.response.AuthenticationRespone;
import com.devteria.identity_service.dto.response.IntrospectRespone;
import com.devteria.identity_service.entity.InvalidatedToken;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.repository.InvalidatedTokenRepository;
import com.devteria.identity_service.repository.UserRepository;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshableDuration;

    public IntrospectRespone introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectRespone.builder().valid(isValid).build();
    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT signedJWT = verifyToken(request.getToken(), true);
            String id = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            var invalidatedToken =
                    InvalidatedToken.builder().id(id).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
            log.info("loqout");
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    @Override
    public AuthenticationRespone refresh(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        var invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signJWT.getJWTClaimsSet().getSubject();
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return AuthenticationRespone.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }

    @Override
    public AuthenticationRespone authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository
                .findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        }

        String token;
        try {
            token = generateToken(user);
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        }
        return AuthenticationRespone.builder().authenticated(true).token(token).build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        var verified = signedJWT.verify(jwsVerifier);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(refreshableDuration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.USER_UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        return signedJWT;
    }

    private String generateToken(User user) throws KeyLengthException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("https://www.facebook.com/profile.php?id=100009348506257")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }

        return stringJoiner.toString();
    }
}
