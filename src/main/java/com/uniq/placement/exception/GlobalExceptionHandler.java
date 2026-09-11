package com.uniq.placement.exception;

import com.uniq.placement.dto.common.ProblemDto;
import com.uniq.placement.dto.common.ValidationProblemDto;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDto> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title("Resource Not Found")
                .detail(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDto> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.CONFLICT.value())
                .title("Conflict")
                .detail(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDto> handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .title("Unprocessable Entity")
                .detail(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationProblemDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        ValidationProblemDto problem = ValidationProblemDto.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .title("Validation Error")
                .detail("Request validation failed")
                .traceId(UUID.randomUUID().toString())
                .errors(errors)
                .build();
        return new ResponseEntity<>(problem, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDto> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .title("Unauthorized")
                .detail("Invalid username or password")
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDto> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .title("Forbidden")
                .detail("You don't have permission to perform this action")
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({DataAccessException.class, JpaSystemException.class, HibernateException.class, PersistenceException.class})
    public ResponseEntity<ProblemDto> handlePersistenceAndDataAccessException(Exception ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Database Error")
                .detail("The request could not be processed because the database query failed. Please contact support with the trace ID.")
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDto> handleGlobalException(Exception ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Internal Server Error")
                .detail("An unexpected error occurred. Please contact support with the trace ID.")
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
