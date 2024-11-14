package com.devteria.identity_service.validator;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = EnumPatternValidator.class)
public @interface EnumPattern {
    String name();
    String regexp();
    String message() default "INVALID_ENUM";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
