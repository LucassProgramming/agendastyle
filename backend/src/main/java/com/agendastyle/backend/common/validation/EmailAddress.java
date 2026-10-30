package com.agendastyle.backend.common.validation;

import java.util.Locale;
import java.util.regex.Pattern;

public final class EmailAddress {

    public static final int MAX_LENGTH = 150;

    private static final String LOCAL_ATOM = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+";
    private static final String LOCAL_PART = LOCAL_ATOM + "(?:\\." + LOCAL_ATOM + ")*";
    private static final String DOMAIN_LABEL = "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?";
    private static final String DOMAIN = DOMAIN_LABEL + "(?:\\." + DOMAIN_LABEL + ")+";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^" + LOCAL_PART + "@" + DOMAIN + "$");

    private EmailAddress() {
    }

    public static String normalize(String email) {
        return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }

    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        String normalizedEmail = normalize(email);

        return !normalizedEmail.isEmpty()
                && normalizedEmail.length() <= MAX_LENGTH
                && EMAIL_PATTERN.matcher(normalizedEmail).matches();
    }
}
