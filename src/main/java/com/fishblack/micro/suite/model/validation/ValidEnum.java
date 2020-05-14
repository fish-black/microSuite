package com.fishblack.micro.suite.model.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "{com.hpe.propel.notification.model.validation.EnumValidator.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}