package com.volodymyrchikh.abitandstudhelp.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserResponseTest {

    @Test
    void userResponseDoesNotExposePassword() {
        boolean hasPasswordField = Arrays.stream(UserResponse.class.getRecordComponents())
                .anyMatch(component -> component.getName().equals("password"));

        assertFalse(hasPasswordField);
    }
}
