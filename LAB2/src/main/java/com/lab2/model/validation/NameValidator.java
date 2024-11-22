package com.lab2.model.validation;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NameValidator implements Validator {
    private static final String NAME_REGEX = "^[A-Z][a-z]+(?:-[A-Z][a-z]+)?\\s[A-Z][a-z]+(?:-[A-Z][a-z]+)?$";
    private static final Pattern pattern;

    static {
        try {
            pattern = Pattern.compile(NAME_REGEX);
        } catch (Throwable thrown) {
            throw new ExceptionInInitializerError(thrown);
        }
    }

    @Override
    public boolean isValid(String name) {
        if (name == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}