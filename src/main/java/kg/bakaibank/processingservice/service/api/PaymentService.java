package kg.bakaibank.processingservice.service.api;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    Payment openPayment(PaymentRequest request,
                        Account debitAccount,
                        Account creditAccount);
    void completePayment(Payment payment);
    void declinePayment(Payment payment, PaymentDeclineReason reason);
    BigDecimal countTodayPaymentSum(UUID debitAccountId);
    int countTodayPayments(UUID debitAccountId);
    Page<PaymentResponse> getCreditPayments(UUID creditAccountId, Pageable pageable);
    Page<PaymentResponse> getDebitPayments(UUID debitAccountId, Pageable pageable);
    PaymentResponse findById(UUID id);
}
