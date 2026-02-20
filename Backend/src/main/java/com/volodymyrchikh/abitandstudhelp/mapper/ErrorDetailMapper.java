package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.dto.ErrorDetail;
import com.volodymyrchikh.abitandstudhelp.exception.EmailIsAlreadyUsed;
import com.volodymyrchikh.abitandstudhelp.exception.FaqNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.FieldAlreadyUsedException;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ErrorDetailMapper {

    @Mapping(target = "cause", expression = "java(\"Email\")")
    @Mapping(target = "message", expression = "java(\"Email=[%s] is already used\".formatted(ex.getEmail()))")
    ErrorDetail from(EmailIsAlreadyUsed ex);

    @Mapping(target = "cause", expression = "java(\"id or name\")")
    @Mapping(target = "message", expression = "java(\"Specialty with id=[%s] or name=[%s] not found\"" +
            ".formatted(ex.getSpecialtyId(), ex.getSpecialtyName()))")
    ErrorDetail from(SpecialtyNotFoundException ex);

    @Mapping(target = "cause", expression = "java(\"FAQ\")")
    @Mapping(target = "message", expression = "java(ex.getMessage())")
    ErrorDetail from(FaqNotFoundException ex);
}
