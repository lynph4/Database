package model.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressValidator {
    private static final String ADDRESS_REGEX = "^[A-Za-z]+(?: [A-Za-z]+)* \\d+$";
    private static final Pattern pattern = Pattern.compile(ADDRESS_REGEX);

    public static boolean isValidAddress(String address) {
        if (address == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }
}