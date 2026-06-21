package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends ApplicationException {
    public AccountNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
