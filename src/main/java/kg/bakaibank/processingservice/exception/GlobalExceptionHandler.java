package kg.bakaibank.processingservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import kg.bakaibank.processingservice.exception.custom.AccountNotFoundException;
import kg.bakaibank.processingservice.exception.custom.ApplicationException;
import kg.bakaibank.processingservice.exception.custom.DefaultTransferLimitNotFoundException;
import kg.bakaibank.processingservice.exception.custom.LimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException e) {

        String customMessage = "Account not found";
        ErrorResponse response = ErrorResponse.builder()
            .message(customMessage)
            .status(e.getStatus().value())
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .build();
        log.info("{}, message: {}",customMessage, e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler(LimitExceededException.class)
    public ResponseEntity<?> handleLimitExceededException(LimitExceededException e) {

        String customMessage = "Limit exceeded";
        ErrorResponse response = ErrorResponse.builder()
            .message(customMessage)
            .status(e.getStatus().value())
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .build();
        log.info("{}, message: {}",customMessage, e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler(DefaultTransferLimitNotFoundException.class)
    public ResponseEntity<?> handleDefaultTransferLimitNotFoundException(DefaultTransferLimitNotFoundException e) {

        String customMessage = "Default transfer limit not found in cards-limit-service";
        ErrorResponse response = ErrorResponse.builder()
            .message(customMessage)
            .status(e.getStatus().value())
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .build();

        log.warn("{}, message: {}",customMessage, e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e,
                                                       HttpServletRequest request) {
        List<ValidationError> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError ->
                ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build()).toList();

        ErrorResponse response = ErrorResponse.builder()
            .error("Validation Failed")
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Arguments didn't pass validation")
            .timestamp(OffsetDateTime.now())
            .validationErrors(errors)
            .path(request.getRequestURI())
            .build();

        log.info("Handled not valid request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        String customMessage = "Invalid format of data";
        ErrorResponse response = ErrorResponse.builder()
            .message(customMessage)
            .status(HttpStatus.BAD_REQUEST.value())
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .build();

        log.info("{}, message: {}", customMessage, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e,
                                                            HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
            .message("Illegal arguments were given")
            .status(HttpStatus.BAD_REQUEST.value())
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .path(request.getRequestURI())
            .build();
        log.info("Illegal arguments were given, message: {}", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException e,
                                                        HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
            .message("Business logic exception")
            .status(e.getStatus().value())
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .path(request.getRequestURI())
            .build();
        log.warn("Not specified app exception handled, message: {}", e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUncaughtException(Exception e,
                                                     HttpServletRequest request) {
        log.error("Uncaught Exception {}", request.getRequestURI(), e);

        ErrorResponse response = ErrorResponse.builder()
            .error(e.getMessage())
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Internal Uncaught Server Error")
            .path(request.getRequestURI())
            .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
