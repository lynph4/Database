package com.lab2.model.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressValidator implements Validator {
    private static final String ADDRESS_REGEX = "^[A-Za-z]+(?: [A-Za-z]+)* \\d+$";
    private static final Pattern pattern;

    static {
        try {
            pattern = Pattern.compile(ADDRESS_REGEX);
        } catch (Throwable thrown) {
            throw new ExceptionInInitializerError(thrown);
        }
    }

    @Override
    public boolean isValid(String address) {
        if (address == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }
}