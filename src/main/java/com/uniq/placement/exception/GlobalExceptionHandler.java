package com.uniq.placement.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.uniq.placement.dto.common.ProblemDto;
import com.uniq.placement.dto.common.ValidationProblemDto;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDto> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request")
                .detail(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String detail = extractReadableMessage(ex);
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Invalid Request")
                .detail(detail)
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid type";
        String detail = String.format("Parameter '%s' with value '%s' could not be converted to %s",
                ex.getName(), ex.getValue(), requiredType);
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Type Mismatch")
                .detail(detail)
                .traceId(UUID.randomUUID().toString())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataAccessException.class, JpaSystemException.class, HibernateException.class, PersistenceException.class})
    public ResponseEntity<ProblemDto> handlePersistenceAndDataAccessException(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Database error. traceId={}", traceId, ex);
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Database Error")
                .detail("Database query failed: " + rootMessage(ex))
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDto> handleGlobalException(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unexpected error. traceId={}", traceId, ex);
        ProblemDto problem = ProblemDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Internal Server Error")
                .detail("Unexpected error: " + rootMessage(ex))
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractReadableMessage(HttpMessageNotReadableException ex) {
        Throwable root = ex.getRootCause();
        if (root instanceof IllegalArgumentException && root.getMessage() != null && !root.getMessage().isBlank()) {
            return root.getMessage();
        }

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String fieldPath = ife.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : ("[" + ref.getIndex() + "]"))
                    .collect(Collectors.joining("."));

            if (ife.getCause() instanceof IllegalArgumentException iae && iae.getMessage() != null && !iae.getMessage().isBlank()) {
                return iae.getMessage();
            }

            Class<?> targetType = ife.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                String accepted = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                return String.format("Invalid value '%s' for field '%s'. Accepted values are: [%s]",
                        ife.getValue(), fieldPath, accepted);
            }

            return String.format("Invalid value '%s' for field '%s' (expected type: %s)",
                    ife.getValue(), fieldPath, targetType != null ? targetType.getSimpleName() : "unknown");
        } else if (cause instanceof ValueInstantiationException vie) {
            if (vie.getCause() instanceof IllegalArgumentException iae && iae.getMessage() != null && !iae.getMessage().isBlank()) {
                return iae.getMessage();
            }
            if (vie.getCause() != null && vie.getCause().getMessage() != null) {
                return vie.getCause().getMessage();
            }
        } else if (cause instanceof MismatchedInputException mie) {
            String fieldPath = mie.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : ("[" + ref.getIndex() + "]"))
                    .collect(Collectors.joining("."));
            return fieldPath.isEmpty() ? "Invalid request format" : String.format("Invalid or missing format for field '%s'", fieldPath);
        } else if (cause instanceof JsonParseException) {
            return "Malformed JSON in request payload";
        }

        if (root != null && root.getMessage() != null && !root.getMessage().isBlank()) {
            String msg = root.getMessage();
            int atIndex = msg.indexOf(" at [Source:");
            if (atIndex != -1) {
                msg = msg.substring(0, atIndex).trim();
            }
            return msg;
        }

        return "Malformed HTTP request body";
    }

    private String rootMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName();
    }
}
