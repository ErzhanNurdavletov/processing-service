package kg.bakaibank.processingservice.mapper;

import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }
        return Payment.builder()
            .amount(request.amount())
            .currency(request.currency())
            .comment(request.comment())
            .build();
    }
}
