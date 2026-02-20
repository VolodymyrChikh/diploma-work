package com.volodymyrchikh.abitandstudhelp.exception.handler;

import com.volodymyrchikh.abitandstudhelp.dto.ErrorDetail;
import com.volodymyrchikh.abitandstudhelp.exception.EmailIsAlreadyUsed;
import com.volodymyrchikh.abitandstudhelp.exception.FieldAlreadyUsedException;
import com.volodymyrchikh.abitandstudhelp.exception.FaqNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.ErrorDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final ErrorDetailMapper errorDetailMapper;

    @ExceptionHandler(EmailIsAlreadyUsed.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleEmailIsAlreadyUsed(EmailIsAlreadyUsed ex, WebRequest request) {
        return getProblemDetail(
                BAD_REQUEST,
                URI.create("about:blank"),
                "Email is already used!",
                URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
                List.of(errorDetailMapper.from(ex))
        );
    }

    @ExceptionHandler(SpecialtyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleSpecialtyNotFound(SpecialtyNotFoundException ex, WebRequest request) {
        return getProblemDetail(
                HttpStatus.NOT_FOUND,
                URI.create("about:blank"),
                "Specialty not found",
                URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
                List.of(errorDetailMapper.from(ex))
        );
    }

    @ExceptionHandler(FaqNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleFaqNotFound(FaqNotFoundException ex, WebRequest request) {
        return getProblemDetail(
                HttpStatus.NOT_FOUND,
                URI.create("about:blank"),
                "FAQ not found",
                URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
                List.of(errorDetailMapper.from(ex))
        );
    }

    private ProblemDetail getProblemDetail(HttpStatus httpStatus, URI type, String title, URI instance,
                                           List<ErrorDetail> details) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        problemDetail.setInstance(instance);
        problemDetail.setProperty("detail", details);
        return problemDetail;
    }

}
