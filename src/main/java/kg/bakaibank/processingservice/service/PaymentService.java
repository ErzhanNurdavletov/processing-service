package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public Payment initPayment(PaymentRequest request,
                               Account debitAccount,
                               Account creditAccount) {
        Payment payment = paymentMapper.toEntity(request);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setDebitAccount(debitAccount);
        payment.setCreditAccount(creditAccount);
        payment.setStatus(PaymentStatus.NEW);
        return paymentRepository.save(payment);
    }

    public void closePayment(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
    }

    public BigDecimal countTodayPaymentSum(UUID debitAccountId) {
        OffsetDateTime dayStartTime = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime dayEndTime = dayStartTime.plusDays(1);
        return paymentRepository.sumAmountForTodayByAccountId(debitAccountId, dayStartTime, dayEndTime);
    }
}
