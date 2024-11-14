package com.devteria.identity_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {
    @Override
    public void initialize(PhoneConstraint phoneConstraint) {
        ConstraintValidator.super.initialize(phoneConstraint);
    }

    @Override
    public boolean isValid(String phoneNo, ConstraintValidatorContext constraintValidatorContext) {
        if (phoneNo == null)
            return false;
        else
            return (phoneNo.matches("^(0|\\+84)(\\d{9})$"));

    }
}
