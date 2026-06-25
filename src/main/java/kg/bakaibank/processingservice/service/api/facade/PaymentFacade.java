package kg.bakaibank.processingservice.service.api.facade;

import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;

import java.util.UUID;

public interface PaymentFacade {
    PaymentShortResponse executePayment(PaymentRequest request, UUID idempotencyKey);
}
