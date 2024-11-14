package com.devteria.identity_service.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1003, "Username must be at least {value} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {value} characters", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1005, "Email must be in correct format", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User doesn't exist", HttpStatus.NOT_FOUND),
    USER_UNAUTHENTICATED(1007, "User not authenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You have not permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "Your age must be at least {value}", HttpStatus.BAD_REQUEST),
    INVALID_REGEX(1010, "Given regex is invalid", HttpStatus.BAD_REQUEST),
    INVALID_ENUM(1011, "must be any of {value}", HttpStatus.BAD_REQUEST),
    ROLES_NOT_EXISTED(1012,"Roles must be has any value of [ADMIN, USER] ", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;
}
