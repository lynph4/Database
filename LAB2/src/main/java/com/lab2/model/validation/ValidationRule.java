package com.lab2.model.validation;

public class ValidationRule {
    private final String value;
    private final Validator validator;
    private final String fieldName;

    public ValidationRule(String value, Validator validator, String fieldName) {
        this.value = value;
        this.validator = validator;
        this.fieldName = fieldName;
    }

    public boolean isValid() {
        return validator.isValid(value);
    }

    public String getErrorMessage() {
        return fieldName + " not passed through " + validator.getClass().getSimpleName();
    }
}