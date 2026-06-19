package kg.bakaibank.processingservice.exception;

import org.springframework.http.HttpStatus;

public class DefaultWithdrawalLimitNotFoundException extends ApplicationException {
    public DefaultWithdrawalLimitNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
