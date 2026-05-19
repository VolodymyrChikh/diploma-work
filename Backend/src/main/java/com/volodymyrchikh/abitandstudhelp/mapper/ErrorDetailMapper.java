package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.dto.ErrorDetail;
import com.volodymyrchikh.abitandstudhelp.exception.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ErrorDetailMapper {

    @Mapping(target = "cause", expression = "java(\"email\")")
    @Mapping(target = "message", expression = "java(\"Ця електронна пошта вже використовується\")")
    ErrorDetail from(EmailIsAlreadyUsed ex);

    @Mapping(target = "cause", expression = "java(\"specialty\")")
    @Mapping(target = "message", expression = "java(\"Спеціальність не знайдено\")")
    ErrorDetail from(SpecialtyNotFoundException ex);

    @Mapping(target = "cause", expression = "java(\"FAQ\")")
    @Mapping(target = "message", expression = "java(ex.getMessage())")
    ErrorDetail from(FaqNotFoundException ex);

    @Mapping(target = "cause", expression = "java(ex.getGroupName())")
    @Mapping(target = "message", expression = "java(ex.getMessage())")
    ErrorDetail from(GroupNotFoundException ex);
}
