package com.volodymyrchikh.abitandstudhelp.exception.handler;

import com.volodymyrchikh.abitandstudhelp.dto.ErrorDetail;
import com.volodymyrchikh.abitandstudhelp.exception.CategoryNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.CommentNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.EmailIsAlreadyUsed;
import com.volodymyrchikh.abitandstudhelp.exception.FieldAlreadyUsedException;
import com.volodymyrchikh.abitandstudhelp.exception.FaqNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.GroupNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.PostNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.WrongCredentialsException;
import com.volodymyrchikh.abitandstudhelp.mapper.ErrorDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

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
                "Електронна пошта вже використовується",
                URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
                List.of(errorDetailMapper.from(ex))
        );
    }

    @ExceptionHandler(WrongCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleWrongCredentials(WrongCredentialsException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Невірні дані для входу");
        problemDetail.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        problemDetail.setDetail(WrongCredentialsException.DEFAULT_DETAIL);
        return problemDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Користувача не знайдено");
        problemDetail.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        problemDetail.setDetail("Користувача не знайдено");
        return problemDetail;
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handlePostNotFound(PostNotFoundException ex, WebRequest request) {
        return getSimpleProblemDetail(HttpStatus.NOT_FOUND, "Допис не знайдено", request);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleCommentNotFound(CommentNotFoundException ex, WebRequest request) {
        return getSimpleProblemDetail(HttpStatus.NOT_FOUND, "Коментар не знайдено", request);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex, WebRequest request) {
        return getSimpleProblemDetail(HttpStatus.NOT_FOUND, "Категорію не знайдено", request);
    }

    @ExceptionHandler(SpecialtyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleSpecialtyNotFound(SpecialtyNotFoundException ex, WebRequest request) {
        return getProblemDetail(
                HttpStatus.NOT_FOUND,
                URI.create("about:blank"),
                "Спеціальність не знайдено",
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
                "Питання не знайдено",
                URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
                List.of(errorDetailMapper.from(ex))
        );
    }

    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleGroupNotFound(GroupNotFoundException ex, WebRequest request) {
        return getProblemDetail(
                HttpStatus.BAD_REQUEST,
                URI.create("about:blank"),
                "Групу не знайдено",
                URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
                List.of(errorDetailMapper.from(ex))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Некоректний запит");
        problemDetail.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(ex.getStatusCode());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle(ex.getStatusCode().is4xxClientError() ? "Некоректний запит" : "Помилка сервера");
        problemDetail.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        problemDetail.setDetail(ex.getReason() == null || ex.getReason().isBlank()
                ? "Не вдалося обробити запит"
                : ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .filter(errorMessage -> errorMessage != null && !errorMessage.isBlank())
                .findFirst()
                .orElse("Запит містить некоректні дані");

        ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Перевірка даних не пройдена");
        problemDetail.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        problemDetail.setDetail(message);
        return problemDetail;
    }

    private ProblemDetail getProblemDetail(HttpStatus httpStatus, URI type, String title, URI instance,
                                           List<ErrorDetail> details) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        problemDetail.setInstance(instance);
        if (!details.isEmpty()) {
            problemDetail.setDetail(details.getFirst().message());
        }
        problemDetail.setProperty("errors", details);
        return problemDetail;
    }

    private ProblemDetail getSimpleProblemDetail(HttpStatus httpStatus, String message, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle(message);
        problemDetail.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        problemDetail.setDetail(message);
        return problemDetail;
    }

}
