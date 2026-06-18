package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;

public interface PaymentFacade {
    PaymentResponse executePayment(PaymentRequest request);
}
