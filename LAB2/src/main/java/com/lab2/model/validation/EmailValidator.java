package com.lab2.model.validation;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailValidator implements Validator {
    private static final String EMAIL_REGEX = "^[\\w\\.\\-]+@[a-zA-Z\\d\\-]+\\.[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})?$";
    private static final Pattern pattern;

    static {
        try {
            pattern = Pattern.compile(EMAIL_REGEX);
        } catch (Throwable thrown) {
            throw new ExceptionInInitializerError(thrown);
        }
    }

    @Override
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
