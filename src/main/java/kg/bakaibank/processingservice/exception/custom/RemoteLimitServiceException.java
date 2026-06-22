package kg.bakaibank.processingservice.exception.custom;

import org.springframework.http.HttpStatus;

public class RemoteLimitServiceException extends ApplicationException {
    public RemoteLimitServiceException(String message, HttpStatus status) {
        super(message, status);
    }
}
