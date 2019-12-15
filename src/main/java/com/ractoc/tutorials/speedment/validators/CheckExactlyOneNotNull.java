package com.ractoc.tutorials.speedment.validators;

import org.apache.commons.beanutils.PropertyUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {CheckExactlyOneNotNull.CheckExactlyOneNotNullValidator.class})
@Documented
public @interface CheckExactlyOneNotNull {

    String message() default "Exactly one field should contain a value";

    // mandatory for a constraints annotation
    Class<?>[] groups() default {};

    // mandatory for a constraints annotation
    Class<? extends Payload>[] payload() default {};

    String[] fieldNames();

    class CheckExactlyOneNotNullValidator implements ConstraintValidator<CheckExactlyOneNotNull, Object> {
        private String[] fieldNames;
        private String message;

        @Override
        public void initialize(CheckExactlyOneNotNull constraintAnnotation) {
            this.fieldNames = constraintAnnotation.fieldNames();
            this.message = constraintAnnotation.message();
        }

        public boolean isValid(Object object, ConstraintValidatorContext context) {
            if (object == null) {
                return true;
            }
            try {
                boolean foundValue = false;
                for (String fieldName : fieldNames) {
                    Object property = PropertyUtils.getProperty(object, fieldName);
                    if (property != null) {
                        if (foundValue) {
                            return false;
                        }
                        foundValue = true;
                    }
                }
                if (!foundValue) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            message)
                            .addConstraintViolation();
                }
                return foundValue;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
