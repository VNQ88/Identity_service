package com.devteria.identity_service.validator;

import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EnumPatternValidator implements ConstraintValidator<EnumPattern, Enum<?>> {
    private Pattern pattern;

    @Override
    public void initialize(EnumPattern enumPattern) {
        try {
            pattern = Pattern.compile(enumPattern.regexp());
        }
        catch (PatternSyntaxException e){
            throw new AppException(ErrorCode.INVALID_REGEX);
        }
    }

    @Override
    public boolean isValid(Enum<?> anEnum, ConstraintValidatorContext constraintValidatorContext) {
        if (anEnum == null)
            return true;
        Matcher m = pattern.matcher(anEnum.name());
        return m.matches();
    }
}
