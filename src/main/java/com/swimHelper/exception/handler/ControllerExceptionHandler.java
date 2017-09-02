package com.swimHelper.exception.handler;

import com.swimHelper.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError("Failed to find user by id");
        logger.error("Failed to find user by id.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = UserExistsException.class)
    protected ResponseEntity<Object> handleUserExistsException(UserExistsException ex, WebRequest request) {
        ApiError apiError = new ApiError("User already exists", ex);
        logger.error("User already exists.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = InvalidUserException.class)
    protected ResponseEntity<Object> handleInvalidUserException(InvalidUserException ex, WebRequest request) {
        ApiError apiError = new ApiError("Invalid user body", ex);
        logger.error("Invalid user body.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = ForbiddenAccessException.class)
    protected ResponseEntity<Object> handleForbiddenAccessException(ForbiddenAccessException ex, WebRequest request) {
        ApiError apiError = new ApiError("Forbidden access", ex);
        logger.error("Forbidden access.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = ExerciseExistsException.class)
    protected ResponseEntity<Object> handleExerciseExistsException(ExerciseExistsException ex, WebRequest request) {
        ApiError apiError = new ApiError("Exercise already exists", ex);
        logger.error("Exercise already exists.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = ExerciseNotFoundException.class)
    protected ResponseEntity<Object> handleExerciseNotFoundException(ExerciseNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError("Failed to find exercise by id");
        logger.error("Failed to find exercise by id.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = InvalidExerciseException.class)
    protected ResponseEntity<Object> handleInvalidExerciseException(InvalidExerciseException ex, WebRequest request) {
        ApiError apiError = new ApiError("Invalid exercise body", ex);
        logger.error("Invalid exercise body.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = MissingTrainingRequirementsException.class)
    protected ResponseEntity<Object> handleMissingTrainingRequirementsException(MissingTrainingRequirementsException ex, WebRequest request) {
        ApiError apiError = new ApiError("Missing training requirements", ex);
        logger.error("Missing training requirements.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = UnsatisfiedTimeRequirementsException.class)
    protected ResponseEntity<Object> handleUnsatisfiedTimeRequirementsException(UnsatisfiedTimeRequirementsException ex, WebRequest request) {
        ApiError apiError = new ApiError("Unsatisfied time requirements", ex);
        logger.error("Unsatisfied time requirements.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = TrainingNotFoundException.class)
    protected ResponseEntity<Object> handleTrainingNotFoundException(TrainingNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError("Failed to find training by id");
        logger.error("Failed to find training by id.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = InvalidTrainingException.class)
    protected ResponseEntity<Object> handleInvalidTrainingException(InvalidTrainingException ex, WebRequest request) {
        ApiError apiError = new ApiError("Invalid training body", ex);
        logger.error("Invalid training body.", ex.getMessage());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
