package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class LimitExceededException extends ApplicationException {
    public LimitExceededException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
