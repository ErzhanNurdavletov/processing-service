package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class AccountWithClientIdExistsException extends ApplicationException {
    public AccountWithClientIdExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
