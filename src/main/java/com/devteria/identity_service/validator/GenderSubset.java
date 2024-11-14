package com.devteria.identity_service.validator;

import com.devteria.identity_service.enums.Gender;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = GenderSubsetValidator.class)
public @interface GenderSubset {
    Gender[] anyOf();
    String message() default "INVALID_ENUM";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
