package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public Payment openPayment(PaymentRequest request,
                               Account debitAccount,
                               Account creditAccount) {
        Payment payment = paymentMapper.toEntity(request);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setDebitAccount(debitAccount);
        payment.setCreditAccount(creditAccount);
        payment.setStatus(PaymentStatus.NEW);
        return paymentRepository.save(payment);
    }

    public void completePayment(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
    }

    public void declinePayment(Payment payment, PaymentDeclineReason reason) {
        payment.setStatus(PaymentStatus.DECLINED);
        payment.setDeclineReason(reason);
        paymentRepository.save(payment);
    }

    public BigDecimal countTodayPaymentSum(UUID debitAccountId) {
        OffsetDateTime dayStartTime = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime dayEndTime = dayStartTime.plusDays(1);
        return paymentRepository.sumAmountForTodayByAccountId(debitAccountId, dayStartTime, dayEndTime);
    }

    public int countTodayPayments(UUID debitAccountId) {
        OffsetDateTime dayStartTime = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime dayEndTime = dayStartTime.plusDays(1);
        return paymentRepository.countPaymentByDebitAccount(debitAccountId, dayStartTime, dayEndTime);
    }
}
