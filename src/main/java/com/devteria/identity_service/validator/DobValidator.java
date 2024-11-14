package com.devteria.identity_service.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstrant, LocalDate> {
    private int min;

    @Override
    public void initialize(DobConstrant constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)) return true;

        int years = (int) ChronoUnit.YEARS.between(localDate, LocalDate.now());
        return years >= min;
    }
}
