package com.devteria.identity_service.exception;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devteria.identity_service.dto.request.ApiRespone;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Handling exception")
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    private static final String REPLACE_PARAM = "value";
    Map<String, Object> attributes = null;
    // Handle fallback exception
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiRespone<Void>> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiRespone<Void> apiRespone = new ApiRespone<>();
        var errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        apiRespone.setCode(errorCode.getCode());
        apiRespone.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiRespone);
    }

    //   Custom exception
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiRespone<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiRespone<Void> apiRespone = new ApiRespone<>();
        apiRespone.setCode(errorCode.getCode());
        apiRespone.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiRespone);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiRespone<Void>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        String minValue = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        } catch (IllegalArgumentException e) {
            //
        }
        ApiRespone<Void> apiRespone = new ApiRespone<>();
        apiRespone.setCode(errorCode.getCode());
        apiRespone.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttributes(errorCode.getMessage(), minValue)
                        : errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiRespone);
    }

    @ExceptionHandler(value =  HttpMessageNotReadableException.class)
    ResponseEntity<ApiRespone<Void>> handlingHttpMessageNotReadableException(HttpMessageNotReadableException e){
        ErrorCode errorCode = ErrorCode.INVALID_ENUM;
        ApiRespone<Void> apiRespone = new ApiRespone<>();
        apiRespone.setCode(errorCode.getCode());
        apiRespone.setMessage("");
        var constraintViolation = e.getLocalizedMessage();
        apiRespone.setMessage(
                Objects.nonNull(constraintViolation)
                        ? mapAttributes(errorCode.getMessage(), extractInvalidEnumValue(constraintViolation))
                        : errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiRespone);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiRespone<Void>> handlingAuthorizationDeniedException(AuthorizationDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiRespone<Void> apiRespone = new ApiRespone<>();
        apiRespone.setCode(errorCode.getCode());
        apiRespone.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiRespone);
    }

    private String mapAttributes(String message, String value) {
        return message.replace("{" + REPLACE_PARAM + "}", value);
    }

    private String extractInvalidEnumValue(String errorMessage) {
        Pattern pattern = Pattern.compile("Enum class: \\[(.*?)\\]");
        Matcher matcher = pattern.matcher(errorMessage);

        if (matcher.find()) {
            // Trích xuất chuỗi trong ngoặc vuông và trả về nguyên vẹn với dấu ngoặc vuông
            String enumValuesStr = matcher.group(0);  // Lấy toàn bộ chuỗi "Enum class: [other, female, male]"
            int startIdx = enumValuesStr.indexOf('['); // Tìm vị trí bắt đầu của dấu '['
            int endIdx = enumValuesStr.indexOf(']');   // Tìm vị trí kết thúc của dấu ']'
            return enumValuesStr.substring(startIdx, endIdx + 1); // Lấy phần trong dấu ngoặc vuông
        }
        return null;
    }
}
