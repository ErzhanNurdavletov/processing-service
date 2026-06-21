package kg.bakaibank.processingservice.service;

//TODO проверка лимита debit карты на количество платежей(нету)
//TODO проверка на наличие денег
//TODO проверка на активность двух карт
//TODO разобраться с @Transactional в платежах
//TODO привязывать счета к транзакциям и платежам ---


import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.exception.custom.DefaultTransferLimitNotFoundException;
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
import org.springframework.transaction.annotation.Isolation;
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

    @Override
    @Transactional(
        isolation = Isolation.SERIALIZABLE,
        timeout = 15,
        rollbackFor = Exception.class
    )
    public PaymentResponse executePayment(PaymentRequest request) {
        RemoteCardResponse sourceCardResponse =
            cardWebClient.getCardById(request.sourceCardId());
        RemoteCardResponse destinationCardResponse =
            cardWebClient.getCardById(request.destinationCardId());

        Account debitAccount = accountService.findById(sourceCardResponse.accountId());
        Account creditAccount = accountService.findById(destinationCardResponse.accountId());
        Payment payment = paymentService.openPayment(request, debitAccount, creditAccount);

        boolean isMoneyEnough = isDebitMoneyEnough(debitAccount, request.amount());
        boolean isLimitExceed = isCardLimitExceed(request.sourceCardId(), debitAccount.getId(), request.amount());
        if (!isMoneyEnough) {
            paymentService.declinePayment(payment, PaymentDeclineReason.INSUFFICIENT_FUNDS);
            return paymentMapper.toResponse(payment);
        }
        if (isLimitExceed) {
            paymentService.declinePayment(payment, PaymentDeclineReason.LIMIT_EXCEEDED);
            return paymentMapper.toResponse(payment);
        }

        Account transitAccount = accountService.getBankTransitAccount();
        Transaction debitToTransitTransaction =
            transactionService.initTransaction(request, debitAccount, transitAccount);
        transferMoney(debitAccount, transitAccount, request.amount());
        transactionService.closeTransaction(debitToTransitTransaction);

        Transaction transitToCreditTransaction =
            transactionService.initTransaction(request, transitAccount, creditAccount);
        transferMoney(transitAccount, creditAccount, request.amount());
        transactionService.closeTransaction(transitToCreditTransaction);

        paymentService.completePayment(payment);
        return paymentMapper.toResponse(payment);
    }

    private void transferMoney(Account debit, Account credit, BigDecimal amount) {
        BigDecimal debitAccountNewBalance = debit.getBalance().subtract(amount);
        BigDecimal creditAccountNewBalance = credit.getBalance().add(amount);
        credit.setBalance(creditAccountNewBalance);
        debit.setBalance(debitAccountNewBalance);
    }

    private boolean isCardLimitExceed(UUID cardId,
                                      UUID debitAccountId,
                                      BigDecimal transferAmount) {
        BigDecimal sumForToday =
            paymentService.countTodayPaymentSum(debitAccountId).add(transferAmount);
        int countForToday = paymentService.countTodayPayments(debitAccountId) + 1;

        RemotePaymentPermissionRequest remoteRequest =
            new RemotePaymentPermissionRequest(sumForToday, countForToday);

        UUID transferLimitId = findCardTransferLimitId(cardId);
        RemotePaymentPermissionResponse response =
            cardWebClient.checkIfPaymentNotExceedLimit(cardId, transferLimitId, remoteRequest);

        return !(response.isAllowedByAmount() && response.isAllowedByCount());
    }

    private UUID findCardTransferLimitId(UUID cardId) {
        List<RemoteCardLimitResponse> cardCurrentLimitsResponses =
            cardWebClient.getCardCurrentLimits(cardId);

        RemoteCardLimitResponse transferLimitResponse = cardCurrentLimitsResponses
            .stream()
            .filter(response -> response.limitName().equals("transfer-limit"))
            .findFirst()
            .orElseThrow(() -> new DefaultTransferLimitNotFoundException("transfer-limit not found for card: " + cardId));

        return transferLimitResponse.limitId();
    }

    private boolean isDebitMoneyEnough(Account debit, BigDecimal amount) {
        return debit.getBalance().compareTo(amount) >= 0;
    }
}
