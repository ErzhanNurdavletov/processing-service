package kg.bakaibank.processingservice.service.implementation.service;

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
import kg.bakaibank.processingservice.service.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
                               Account creditAccount,
                               UUID idempotencyKey) {
        Payment payment = paymentMapper.toEntity(request);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setDebitAccount(debitAccount);
        payment.setCreditAccount(creditAccount);
        payment.setStatus(PaymentStatus.NEW);
        payment.setIdempotencyKey(idempotencyKey);
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
    public PaymentResponse findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("PaymentId: " + id + " not found"));
        return paymentMapper.toResponse(payment);
    }

    @Override
    public Page<PaymentShortResponse> getPayments(UUID accountId,
                                                      Pageable pageable,
                                                      PaymentAccountType type,
                                                      OffsetDateTime from,
                                                      OffsetDateTime to) {
        Page<Payment> paymentPage = switch (type) {
            case DEBIT -> paymentRepository.findByDebitAccountId(accountId, from, to, pageable);
            case CREDIT -> paymentRepository.findByCreditAccountId(accountId, from, to, pageable);
            case null -> paymentRepository.findByAccountId(accountId, from, to, pageable);
        };
        return paymentPage.map(paymentMapper::toShortResponse);
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(UUID idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Override
    public boolean isRequestEqualsPayment(PaymentRequest request, Payment payment) {
        boolean isAmountEquals = request.amount().compareTo(payment.getAmount()) == 0;
        boolean isSourceCardEquals = request.sourceCardId().equals(payment.getSourceCardId());
        boolean isDestinationCardEquals = request.destinationCardId().equals(payment.getDestinationCardId());
        boolean isCommentEquals = request.comment().equals(payment.getComment());
        boolean isCurrencyEquals = request.currency().equals(payment.getCurrency());
        return isAmountEquals && isSourceCardEquals
            && isDestinationCardEquals && isCommentEquals && isCurrencyEquals;
    }
}
