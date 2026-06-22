package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class CardIsBlockedException extends ApplicationException {
    public CardIsBlockedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
