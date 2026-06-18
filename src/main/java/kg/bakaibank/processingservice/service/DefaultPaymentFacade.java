package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultPaymentFacade implements PaymentFacade {

    private final PaymentService paymentService;
    private final AccountService accountService;

    @Override
    @Transactional
    public PaymentResponse executePayment(PaymentRequest request) {
        Account debitAccount = accountService.findById(request.sourceAccountId());
        Account creditAccount = accountService.findById(request.destinationAccountId());

        Payment payment = paymentService.initPayment(request, debitAccount, creditAccount);
        return null;
    }
}
