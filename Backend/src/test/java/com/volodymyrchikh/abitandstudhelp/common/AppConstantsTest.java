package com.volodymyrchikh.abitandstudhelp.common;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConstantsTest {

    @Test
    void passwordRegexAllowsStrongPasswordsWithSpecialCharacters() {
        assertTrue(Pattern.matches(AppConstants.PASSWORD_REGEX, "48hb9j1#xtr1Dy!P7M20"));
    }

    @Test
    void passwordRegexRejectsPasswordsWithoutRequiredCharacterClasses() {
        assertFalse(Pattern.matches(AppConstants.PASSWORD_REGEX, "password"));
        assertFalse(Pattern.matches(AppConstants.PASSWORD_REGEX, "PASSWORD1"));
        assertFalse(Pattern.matches(AppConstants.PASSWORD_REGEX, "Password"));
    }
}
