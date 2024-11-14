package com.devteria.identity_service.validator;

import com.devteria.identity_service.enums.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class GenderSubsetValidator implements ConstraintValidator<GenderSubset, Gender> {
    private Gender[] genders;
    @Override
    public void initialize(GenderSubset constraint) {
        this.genders = constraint.anyOf();
    }

    @Override
    public boolean isValid(Gender gender, ConstraintValidatorContext constraintValidatorContext) {
        return gender == null || Arrays.asList(genders).contains(gender);
    }
}
