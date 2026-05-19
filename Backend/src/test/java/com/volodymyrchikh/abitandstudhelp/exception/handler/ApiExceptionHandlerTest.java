package com.volodymyrchikh.abitandstudhelp.exception.handler;

import com.volodymyrchikh.abitandstudhelp.exception.CategoryNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.CommentNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.PostNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.WrongCredentialsException;
import com.volodymyrchikh.abitandstudhelp.mapper.ErrorDetailMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler(mock(ErrorDetailMapper.class));

    @Test
    void wrongCredentialsResponseIsGenericAndUkrainian() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/authenticate");

        ProblemDetail problemDetail = handler.handleWrongCredentials(
                new WrongCredentialsException("Incorrect password", "Email or password is incorrect"),
                new ServletWebRequest(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals("Невірні дані для входу", problemDetail.getTitle());
        assertEquals(WrongCredentialsException.DEFAULT_DETAIL, problemDetail.getDetail());
        assertFalse(problemDetail.getDetail().contains("Email"));
    }

    @Test
    void responseStatusExceptionKeepsHumanReasonInProblemDetail() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/media");

        ResponseEntity<ProblemDetail> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Додайте хоча б одне посилання на файл"),
                new ServletWebRequest(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Некоректний запит", response.getBody().getTitle());
        assertEquals("Додайте хоча б одне посилання на файл", response.getBody().getDetail());
    }

    @Test
    void postNotFoundResponseIsGenericAndUkrainian() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts/404");

        ProblemDetail problemDetail = handler.handlePostNotFound(
                new PostNotFoundException("Post of id=[404] not found", 404L),
                new ServletWebRequest(request)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Допис не знайдено", problemDetail.getTitle());
        assertEquals("Допис не знайдено", problemDetail.getDetail());
        assertFalse(problemDetail.getDetail().contains("Post"));
    }

    @Test
    void commentNotFoundResponseIsGenericAndUkrainian() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/comments/404");

        ProblemDetail problemDetail = handler.handleCommentNotFound(
                new CommentNotFoundException("Comment of id=[404] not found", 404L),
                new ServletWebRequest(request)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Коментар не знайдено", problemDetail.getTitle());
        assertEquals("Коментар не знайдено", problemDetail.getDetail());
        assertFalse(problemDetail.getDetail().contains("Comment"));
    }

    @Test
    void categoryNotFoundResponseIsGenericAndUkrainian() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/posts");

        ProblemDetail problemDetail = handler.handleCategoryNotFound(
                new CategoryNotFoundException("Category not found", 404L),
                new ServletWebRequest(request)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Категорію не знайдено", problemDetail.getTitle());
        assertEquals("Категорію не знайдено", problemDetail.getDetail());
        assertFalse(problemDetail.getDetail().contains("Category"));
    }
}
