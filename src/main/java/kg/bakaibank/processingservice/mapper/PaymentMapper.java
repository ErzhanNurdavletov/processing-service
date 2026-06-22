package kg.bakaibank.processingservice.mapper;

import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
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

    public PaymentShortResponse toShortResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentShortResponse(
            payment.getId(),
            payment.getStatus(),
            payment.getDeclineReason());
    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
            payment.getId(),
            payment.getDebitAccount().getId(),
            payment.getCreditAccount().getId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            payment.getCreatedAt(),
            payment.getComment(),
            payment.getDeclineReason()
            );
    }
}
