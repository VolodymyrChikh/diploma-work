package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.dto.ErrorDetail;
import com.volodymyrchikh.abitandstudhelp.exception.EmailIsAlreadyUsed;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ErrorDetailMapperTest {

    private final ErrorDetailMapper mapper = Mappers.getMapper(ErrorDetailMapper.class);

    @Test
    void mapsUsedEmailToHumanUkrainianMessageWithoutLeakingEmail() {
        String email = "student@example.com";

        ErrorDetail detail = mapper.from(new EmailIsAlreadyUsed("Email is already used", email));

        assertEquals("email", detail.cause());
        assertEquals("Ця електронна пошта вже використовується", detail.message());
        assertFalse(detail.message().contains(email));
    }
}
