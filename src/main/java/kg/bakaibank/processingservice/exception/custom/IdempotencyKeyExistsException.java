package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class IdempotencyKeyExistsException extends ApplicationException {
    public IdempotencyKeyExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
