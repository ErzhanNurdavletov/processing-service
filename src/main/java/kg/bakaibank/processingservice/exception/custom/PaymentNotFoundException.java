package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends ApplicationException {
    public PaymentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
