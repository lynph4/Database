package model.validation;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NameValidator {
    private static final String NAME_REGEX = "^[A-Z][a-z]+(?:-[A-Z][a-z]+)?\\s[A-Z][a-z]+(?:-[A-Z][a-z]+)?$";
    private static final Pattern pattern = Pattern.compile(NAME_REGEX);

    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}