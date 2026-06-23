package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import kg.bakaibank.processingservice.exception.custom.PaymentNotFoundException;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.repository.PaymentRepository;
import kg.bakaibank.processingservice.service.api.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
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

    @Override
    public void completePayment(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
    }

    @Override
    public void declinePayment(Payment payment, PaymentDeclineReason reason) {
        payment.setStatus(PaymentStatus.DECLINED);
        payment.setDeclineReason(reason);
        paymentRepository.save(payment);
    }

    @Override
    public BigDecimal countTodayPaymentSum(UUID debitAccountId) {
        OffsetDateTime dayStartTime = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime dayEndTime = dayStartTime.plusDays(1);
        return paymentRepository.sumAmountForTodayByAccountId(debitAccountId,
            dayStartTime, dayEndTime, Set.of(PaymentStatus.COMPLETED, PaymentStatus.NEW));
    }

    @Override
    public int countTodayPayments(UUID debitAccountId) {
        OffsetDateTime dayStartTime = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime dayEndTime = dayStartTime.plusDays(1);
        return paymentRepository.countPaymentByDebitAccount(debitAccountId,
            dayStartTime, dayEndTime, Set.of(PaymentStatus.COMPLETED, PaymentStatus.NEW));
    }

    @Override
    public Page<PaymentShortResponse> getPayments(UUID accountId,
                                                  Pageable pageable,
                                                  PaymentAccountType type,
                                                  OffsetDateTime from,
                                                  OffsetDateTime to) {
        Page<Payment> paymentPage;

        if (type == PaymentAccountType.CREDIT) {
            paymentPage = paymentRepository.findByCreditAccountId(accountId, from, to, pageable);
        } else if (type == PaymentAccountType.DEBIT) {
            paymentPage = paymentRepository.findByDebitAccountId(accountId, from, to, pageable);
        } else {
            paymentPage = paymentRepository.findByAccountId(accountId, from, to, pageable);
        }
        return paymentPage.map(paymentMapper::toShortResponse);
    }

    @Override
    public PaymentResponse findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("PaymentId: " + id + " not found"));
        return paymentMapper.toResponse(payment);
    }
}
