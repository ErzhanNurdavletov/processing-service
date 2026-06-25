package kg.bakaibank.processingservice.service.implementation.facade;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class DefaultPaymentFacadeTest {

    @Mock
    private PaymentService paymentService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private CardWebClient cardWebClient;
    @Mock
    private AccountService accountService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private PaymentMapper paymentMapper;
    @InjectMocks
    private DefaultPaymentFacade paymentFacade;

    @Test
    public void executePayment_validRequest_successfulPayment() {
        // ARRANGE
        UUID sourceCardId = UUID.randomUUID();
        UUID destinationCardId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(100);
        PaymentRequest request = new PaymentRequest(sourceCardId, destinationCardId, amount,
            PaymentCurrency.SOM, "test-payment");

        // Подготовка всех мокировок ДО вызова
        RemoteCardResponse sourceCard = new RemoteCardResponse(sourceCardId, null, null, null,
            CardStatus.ACTIVE, null, null, null, UUID.randomUUID());
        RemoteCardResponse destCard = new RemoteCardResponse(destinationCardId, null, null, null,
            CardStatus.ACTIVE, null, null, null, UUID.randomUUID());

        Account debitAcc = Account.builder().id(sourceCard.accountId()).balance(new BigDecimal(1000)).build();
        Account creditAcc = Account.builder().id(destCard.accountId()).balance(BigDecimal.ZERO).build();
        Account transitAcc = Account.builder().balance(BigDecimal.ZERO).build();

        Payment payment = Payment.builder().id(UUID.randomUUID()).build();
        PaymentShortResponse expectedResponse = new PaymentShortResponse(payment.getId(), amount, null, null);

        Mockito.when(cardWebClient.getCardById(sourceCardId)).thenReturn(sourceCard);
        Mockito.when(cardWebClient.getCardById(destinationCardId)).thenReturn(destCard);
        Mockito.when(paymentService.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        Mockito.when(accountService.findByIdForUpdate(sourceCard.accountId())).thenReturn(debitAcc);
        Mockito.when(accountService.findByIdForUpdate(destCard.accountId())).thenReturn(creditAcc);
        Mockito.when(paymentService.openPayment(request, debitAcc, creditAcc, null)).thenReturn(payment);

        Mockito.when(paymentService.countTodayPaymentSum(debitAcc.getId())).thenReturn(BigDecimal.ZERO);
        Mockito.when(paymentService.countTodayPayments(debitAcc.getId())).thenReturn(0);

        List<RemoteCardLimitResponse> limits = List.of(
            new RemoteCardLimitResponse(UUID.randomUUID(), "transfer-limit", new BigDecimal(100_000), 1000)
        );
        Mockito.when(cardWebClient.getCardCurrentLimits(sourceCardId)).thenReturn(limits);
        Mockito.when(cardWebClient.checkIfPaymentNotExceedLimit(
                eq(sourceCardId), any(UUID.class), any(RemotePaymentPermissionRequest.class)))
            .thenReturn(new RemotePaymentPermissionResponse(true, true));

        Mockito.when(accountService.getBankTransitAccount()).thenReturn(transitAcc);
        Transaction debitToTransit = Transaction.builder().build();
        Transaction transitToCredit = Transaction.builder().build();
        Mockito.when(transactionService.initTransaction(eq(request), eq(debitAcc), eq(transitAcc), eq(payment)))
            .thenReturn(debitToTransit);
        Mockito.when(transactionService.initTransaction(eq(request), eq(transitAcc), eq(creditAcc), eq(payment)))
            .thenReturn(transitToCredit);

        Mockito.when(paymentMapper.toShortResponse(payment)).thenReturn(expectedResponse);

        // ACT
        PaymentShortResponse actualResponse = paymentFacade.executePayment(request, null);

        // ASSERT
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedResponse.paymentId(), actualResponse.paymentId());
        Assertions.assertEquals(amount, actualResponse.amount());

        // Проверка вызовов критических методов
        Mockito.verify(transactionService, Mockito.times(2)).closeTransaction(any());
        Mockito.verify(paymentService).completePayment(payment);
        Mockito.verify(outboxService).createOutbox(payment);
    }
}
