package com.devteria.identity_service.validator;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstrant {
    String message() default "Invalid date of birth";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min();
}
