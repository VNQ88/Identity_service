package com.devteria.identity_service.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // Sử dụng JwtGrantedAuthoritiesConverter để lấy authorities nếu cần
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        AbstractAuthenticationToken authenticationToken = jwtAuthenticationConverter.convert(jwt);

        // Lấy userId từ claims và gán vào Principal
        if (authenticationToken != null) {
            String userId = jwt.getClaim("userId"); // lấy userId từ claim
            authenticationToken.setDetails(userId);
        }

        return authenticationToken;
    }
}

