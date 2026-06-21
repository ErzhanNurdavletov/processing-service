package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class DefaultTransferLimitNotFoundException extends ApplicationException {
    public DefaultTransferLimitNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
