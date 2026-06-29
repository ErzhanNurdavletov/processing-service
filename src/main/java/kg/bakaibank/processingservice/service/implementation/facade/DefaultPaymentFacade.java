package kg.bakaibank.processingservice.service.implementation.facade;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.exception.custom.CardIsBlockedException;
import kg.bakaibank.processingservice.exception.custom.DefaultTransferLimitNotFoundException;
import kg.bakaibank.processingservice.exception.custom.IdempotencyKeyExistsException;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.service.api.facade.PaymentFacade;
import kg.bakaibank.processingservice.service.api.service.AccountService;
import kg.bakaibank.processingservice.service.api.service.OutboxService;
import kg.bakaibank.processingservice.service.api.service.PaymentService;
import kg.bakaibank.processingservice.service.api.service.TransactionService;
import kg.bakaibank.processingservice.webclient.CardWebClient;
import kg.bakaibank.processingservice.webclient.payload.enums.CardStatus;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPaymentFacade implements PaymentFacade {

    private final PaymentService paymentService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final OutboxService outboxService;
    private final CardWebClient cardWebClient;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(
        timeout = 15,
        rollbackFor = Exception.class
    )
    public PaymentShortResponse executePayment(PaymentRequest request, UUID idempotencyKey) {
        log.info("request {}", request);
        log.info("idempotencyKey {}", idempotencyKey);
        Optional<Payment> existsPayment = paymentService.findByIdempotencyKey(idempotencyKey);
        if (existsPayment.isPresent()) {
            if (!paymentService.isRequestEqualsPayment(request, existsPayment.get())) {
                throw new IdempotencyKeyExistsException(
                    "Idempotency-Key: " + idempotencyKey + " exists");
            }
            return paymentMapper.toShortResponse(existsPayment.get());
        }
        RemoteCardResponse sourceCardResponse = cardWebClient.getCardById(request.sourceCardId());
        RemoteCardResponse destinationCardResponse = cardWebClient.getCardById(request.destinationCardId());
        log.info("sourceCardResponse {}", sourceCardResponse);
        log.info("destinationCardResponse {}", destinationCardResponse);
        checkIfCardBlocked(sourceCardResponse);
        checkIfCardBlocked(destinationCardResponse);

        Account debitAccount = accountService.findByIdForUpdate(sourceCardResponse.accountId());
        Account creditAccount = accountService.findByIdForUpdate(destinationCardResponse.accountId());
        Payment payment = paymentService.openPayment(request, debitAccount, creditAccount, idempotencyKey);
        log.info("debitAccount {}", debitAccount);
        log.info("creditAccount {}", creditAccount);
        log.info("payment {}", payment);

        boolean isMoneyEnough = isDebitMoneyEnough(debitAccount, request.amount());
        if (!isMoneyEnough) {
            log.info("INSUFFICIENT_FUNDS for card with id: {}", request.sourceCardId());
            paymentService.declinePayment(payment, PaymentDeclineReason.INSUFFICIENT_FUNDS);
            log.info("saved declined payment with id: {}", payment.getId());
            outboxService.createOutbox(payment);
            return paymentMapper.toShortResponse(payment);
        }

        boolean isLimitExceed = isCardLimitExceed(request.sourceCardId(), debitAccount.getId(), request.amount());
        if (isLimitExceed) {
            paymentService.declinePayment(payment, PaymentDeclineReason.LIMIT_EXCEEDED);
            outboxService.createOutbox(payment);
            return paymentMapper.toShortResponse(payment);
        }

        Account transitAccount = accountService.getBankTransitAccount();
        Transaction debitToTransitTransaction =
            transactionService.initTransaction(request, debitAccount, transitAccount, payment);
        transferMoney(debitAccount, transitAccount, request.amount());
        transactionService.closeTransaction(debitToTransitTransaction);

        Transaction transitToCreditTransaction =
            transactionService.initTransaction(request, transitAccount, creditAccount, payment);
        transferMoney(transitAccount, creditAccount, request.amount());
        transactionService.closeTransaction(transitToCreditTransaction);

        paymentService.completePayment(payment);
        outboxService.createOutbox(payment);
        log.info("payment before returning response: {}", payment);
        return paymentMapper.toShortResponse(payment);
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
        log.info("cardId {}", cardId);
        log.info("debitAccountId {}", debitAccountId);
        BigDecimal sumForToday =
            paymentService.countTodayPaymentSum(debitAccountId).add(transferAmount);
        int countForToday = paymentService.countTodayPayments(debitAccountId); // здесь был инкремент

        log.info("sumForToday {}, countForToday {}", sumForToday, countForToday);
        RemotePaymentPermissionRequest remoteRequest =
            new RemotePaymentPermissionRequest(sumForToday, countForToday);

        log.info("RemotePaymentPermissionRequest {}", remoteRequest);
        UUID transferLimitId = findCardTransferLimitId(cardId);
        RemotePaymentPermissionResponse response =
            cardWebClient.checkIfPaymentNotExceedLimit(cardId, transferLimitId, remoteRequest);

        log.info("RemotePaymentPermissionResponse {}", response);
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

    private void checkIfCardBlocked(RemoteCardResponse cardResponse) {
        if (cardResponse.status() != CardStatus.ACTIVE) {
            throw new CardIsBlockedException("Card with id: " + cardResponse.id() + " is blocked");
        }
    }
}
