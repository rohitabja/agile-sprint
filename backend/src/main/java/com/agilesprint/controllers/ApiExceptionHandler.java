package com.agilesprint.controllers;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agilesprint.services.ServiceExceptions.Forbidden;
import com.agilesprint.services.ServiceExceptions.NotFound;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(NotFound exception, ServerHttpRequest request) {
        log.error("Request failed: method={}, path={}, status=404, error={}",
                request.getMethod(), request.getURI().getPath(), exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(Forbidden.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> forbidden(Forbidden exception, ServerHttpRequest request) {
        log.error("Request failed: method={}, path={}, status=403, error={}",
                request.getMethod(), request.getURI().getPath(), exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException exception, ServerHttpRequest request) {
        log.error("Request failed: method={}, path={}, status=400, error={}",
                request.getMethod(), request.getURI().getPath(), exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> unexpected(Exception exception, ServerHttpRequest request) {
        log.error("Unhandled request exception: method={}, path={}, status=500",
                request.getMethod(), request.getURI().getPath(), exception);
        return Map.of("error", "An unexpected error occurred");
    }
}
