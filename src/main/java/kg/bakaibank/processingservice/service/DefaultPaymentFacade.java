package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.exception.DefaultWithdrawalLimitNotFoundException;
import kg.bakaibank.processingservice.exception.LimitExceededException;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.webclient.CardWebClient;
import kg.bakaibank.processingservice.webclient.payload.request.RemotePaymentPermissionRequest;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardLimitResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemotePaymentPermissionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPaymentFacade implements PaymentFacade {

    private final PaymentService paymentService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardWebClient cardWebClient;
    private final PaymentMapper paymentMapper;


    public void method(PaymentRequest request) {
        RemoteCardResponse sourceCardResponse =
            cardWebClient.getCardById(request.sourceCardId());
        RemoteCardResponse destinationCardResponse =
            cardWebClient.getCardById(request.destinationCardId());

        checkIfCardLimitExceed(request.sourceCardId(), sourceCardResponse.accountId());


    }

    @Override
    @Transactional
    public PaymentResponse executePayment(PaymentRequest request) {
        RemoteCardResponse sourceCardResponse =
            cardWebClient.getCardById(request.sourceCardId());
        RemoteCardResponse destinationCardResponse =
            cardWebClient.getCardById(request.destinationCardId());

        checkIfCardLimitExceed(request.sourceCardId(), sourceCardResponse.accountId());

        Account debitAccount = accountService.findById(sourceCardResponse.accountId());
        Account creditAccount = accountService.findById(destinationCardResponse.accountId());

        Payment payment = paymentService.initPayment(request, debitAccount, creditAccount);

        Account transitAccount = accountService.getBankTransitAccount();

        Transaction debitToTransitTransaction =
            transactionService.initTransaction(request, debitAccount, transitAccount);
        transferMoney(debitAccount, transitAccount, request.amount());
        transactionService.closeTransaction(debitToTransitTransaction);

        Transaction transitToCreditTransaction =
            transactionService.initTransaction(request, transitAccount, creditAccount);
        transferMoney(transitAccount, creditAccount, request.amount());
        transactionService.closeTransaction(transitToCreditTransaction);

        paymentService.closePayment(payment);
        return paymentMapper.toResponse(payment);
    }

    private void transferMoney(Account debit, Account credit, BigDecimal amount) {
        BigDecimal debitAccountNewBalance = debit.getBalance().subtract(amount);
        BigDecimal creditAccountNewBalance = credit.getBalance().add(amount);
        credit.setBalance(creditAccountNewBalance);
        debit.setBalance(debitAccountNewBalance);
    }

    private void checkIfCardLimitExceed(UUID cardId, UUID debitAccountId) {
        List<RemoteCardLimitResponse> cardCurrentLimitsResponses =
            cardWebClient.getCardCurrentLimits(cardId);

        RemoteCardLimitResponse withdrawalLimitResponse = cardCurrentLimitsResponses
            .stream()
            .filter(response -> response.limitName().equals("withdrawal-limit"))
            .findFirst()
            .orElseThrow(() -> new DefaultWithdrawalLimitNotFoundException("withdrawal-limit not found for card: " + cardId));

        UUID withdrawalLimitId = withdrawalLimitResponse.limitId();
        BigDecimal amountForToday = paymentService.countTodayPaymentSum(debitAccountId);
        RemotePaymentPermissionRequest remoteRequest = new RemotePaymentPermissionRequest(amountForToday);
        RemotePaymentPermissionResponse response =
            cardWebClient.checkIfPaymentNotExceedLimit(cardId, withdrawalLimitId, remoteRequest);

        if (!response.isAllowed()) {
            throw new LimitExceededException("LIMIT_EXCEEDED for card: " + cardId);
        }
    }
}
