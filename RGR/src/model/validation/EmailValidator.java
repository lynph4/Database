package model.validation;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[\\w\\.\\-]+@[a-zA-Z\\d\\-]+\\.[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})?$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
