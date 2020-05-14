package com.fishblack.micro.suite.model.validation;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private List<String> valueList;
    private Class<? extends Enum<?>> enumClass;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        if (!valueList.contains(value.toUpperCase())) {
            String errMsg = "Invalid " + enumClass.getSimpleName().toLowerCase() + ": ";
            errMsg += "\"" + value + "\"" + ". ";
            errMsg += " Valid " + enumClass.getSimpleName().toLowerCase() + " values are: ";
            errMsg += Joiner.on(", ").join(valueList);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errMsg)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        valueList = new ArrayList<>();
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        this.enumClass = enumClass;
        Enum[] enumValArr = enumClass.getEnumConstants();

        for (Enum enumVal : enumValArr) {
            valueList.add(enumVal.toString().toUpperCase());
        }
    }
}