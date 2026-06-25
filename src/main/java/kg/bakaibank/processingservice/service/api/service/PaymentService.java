package kg.bakaibank.processingservice.service.api.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    Payment openPayment(PaymentRequest request,
                        Account debitAccount,
                        Account creditAccount,
                        UUID idempotencyKey);
    void completePayment(Payment payment);
    void declinePayment(Payment payment, PaymentDeclineReason reason);
    BigDecimal countTodayPaymentSum(UUID debitAccountId);
    int countTodayPayments(UUID debitAccountId);
    PaymentResponse findById(UUID id);
    Page<PaymentShortResponse> getPayments(UUID accountId,
                                           Pageable pageable,
                                           PaymentAccountType type,
                                           OffsetDateTime from,
                                           OffsetDateTime to);
    Optional<Payment> findByIdempotencyKey(UUID idempotencyKey);
    boolean isRequestEqualsPayment(PaymentRequest request, Payment payment);
}
