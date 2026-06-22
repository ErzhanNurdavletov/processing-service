package kg.bakaibank.processingservice.service.api;

import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;

public interface PaymentFacade {
    PaymentShortResponse executePayment(PaymentRequest request);
}
