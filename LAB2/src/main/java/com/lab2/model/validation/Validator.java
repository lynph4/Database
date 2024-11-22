package com.lab2.model.validation;

@FunctionalInterface
public interface Validator {
    boolean isValid(String value);
}