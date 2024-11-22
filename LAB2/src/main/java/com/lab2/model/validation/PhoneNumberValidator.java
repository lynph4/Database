package com.lab2.model.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements Validator {
    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final Pattern pattern;

    static {
        try {
            pattern = Pattern.compile(PHONE_REGEX);
        } catch (Throwable thrown) {
            throw new ExceptionInInitializerError(thrown);
        }
    }

    @Override
    public boolean isValid(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
