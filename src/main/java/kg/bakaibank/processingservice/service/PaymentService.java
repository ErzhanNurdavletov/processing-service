package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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
        return paymentRepository.save(payment);
    }
}
